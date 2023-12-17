package com.github.phillbarber.conductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.facade.FacadeLanucher;
import com.github.phillbarber.conductor.stubs.StubServices;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@WireMockTest
public class EndToEndTest {

    public static final String WORKFLOW_JSON_FILE = "workflows/car-order-workflow.json";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String restFacadeURL = "http://localhost:8080";
    public static final String restFacadeOrderURL = restFacadeURL + "/order";
    private static Network network = Network.newNetwork();

    @Container
    private static GenericContainer redis = getRedisContainer();

    @Container
    private  static GenericContainer elastic = getElasticSearchContainer();

    @Container
    private static GenericContainer conductorServer = getConductorContainer();

    @Container
    private static GenericContainer conductorUI = getConductorUIContainer();

    private static FacadeLanucher facadeLanucher = null;

    private final HttpClient httpClient = HttpClientBuilder.create().build();

    private static Workers workers;
    private StubServices stubServices = new StubServices();;

    @BeforeAll
    public static void start(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        workers = startWorkers(getConductorServerURL(), wmRuntimeInfo.getHttpBaseUrl() );
        initialiseWorkflow();

        assertTrue(redis.isRunning());
        assertTrue(conductorServer.isRunning());
        assertTrue(elastic.isRunning());
        assertTrue(conductorUI.isRunning());

        facadeLanucher = new FacadeLanucher(restFacadeURL, getConductorServerURL());
        new Thread(() -> facadeLanucher.startServer()).start();
        assertTrue(facadeLanucher.isRunning());
    }

    @Test
    public void happyPathOrder() throws IOException {
        stubServices.orderServiceReturnsValidOrderFor("Blista");
        stubServices.saveOrderReturnsOK();
        stubServices.priceServiceReturnsPrice();
        stubServices.customerServiceReturnsCustomerFor("12345");
        stubServices.discountServiceReturns();

        Map order = (Map) submitOrderToRestFacade(getHappyPathInput()).get("order");

        assertNotNull(order.get("id"));
        assertNotNull(order.get("customerId"));
        assertNotNull(order.get("customerName"));
        assertNotNull(order.get("customerLoyaltyPoints"));
        assertEquals(order.get("basePrice"), 60000);
        assertEquals(order.get("totalPrice"), 54000);
        assertEquals(order.get("currency"), "GBP");
        assertEquals(order.get("promotionCode"), "ABCDE1234");
        assertEquals(order.get("discount"), 0.1);
    }

    private Map submitOrderToRestFacade(HashMap happyPathInput) {

        Map order;
        HttpPost httpPost = new HttpPost(restFacadeOrderURL);
        try {

            httpPost.setEntity(new StringEntity(OBJECT_MAPPER.writer().writeValueAsString(happyPathInput)));
            httpPost.setHeader(new BasicHeader("content-type", "application/json"));
            String execute = httpClient.execute(httpPost, new BasicHttpClientResponseHandler());
            order = OBJECT_MAPPER.reader().readValue(execute, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    @Test
    @Ignore
    public void unHappyPathOrder() throws IOException, InterruptedException {
        stubServices.orderServiceReturnsInvalidOrderFor("Sentinel");
        Map orderResponse = submitOrderToRestFacade(getUnHappyPathInput());
        assertNull(orderResponse.get("orderId"));
        assertNotNull(orderResponse.get("rejection"));
    }

    @AfterAll
    public static void stop(){
        workers.shutdown();
    }

    private void waitForWorkflowToFinish(String workflowId) {
        await()
                .atLeast(Duration.of(1, ChronoUnit.SECONDS))
                .atMost(Duration.of(10, ChronoUnit.MINUTES))
                .with()
                .pollInterval(Duration.of(1, ChronoUnit.SECONDS))
                .until(() -> getWorkflowClient().getWorkflow(workflowId, true).getStatus() == Workflow.WorkflowStatus.COMPLETED);
    }

    private static HashMap getHappyPathInput() throws IOException {
        return new ObjectMapper().readValue("""
                {
                  "order" : {
                    "car" : {
                      "make": "Blista",
                      "model": "Compact"
                    },
                    "customer" :{
                      "id" : "12345"
                    }
                  }
                }
                """, HashMap.class);
    }

    private static HashMap getUnHappyPathInput() throws IOException {
        return new ObjectMapper().readValue("""
                {
                  "order" : {
                    "car" : {
                      "make": "Sentinel",
                      "model": "someModel"
                    },
                    "customer" :{
                      "id" : "12345"
                    }
                  }
                }
                """, HashMap.class);
    }

    private static Workers startWorkers(String conductorServerURL, String serviceRootURI) {
        Workers workers = new Workers(conductorServerURL, serviceRootURI);
        new Thread(workers::start).start();
        return workers;
    }

    private String startWorkflow(HashMap input) {
        WorkflowClient workflowClient = getWorkflowClient();
        return workflowClient.startWorkflow(getStartWorkflowRequest(input));
    }

    @NotNull
    private WorkflowClient getWorkflowClient() {
        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(getConductorServerURL());
        return workflowClient;
    }

    @NotNull
    private static StartWorkflowRequest getStartWorkflowRequest(HashMap input) {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName("CarOrderWorkflow");
        startWorkflowRequest.setInput(input);
        return startWorkflowRequest;
    }

    private static void initialiseWorkflow() throws IOException {
        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(getConductorServerURL());
        metadataClient.registerWorkflowDef(getWorkflowDef());
    }

    @NotNull
    private static String getConductorServerURL() {
        return "http://localhost:" + conductorServer.getMappedPort(8080) + "/api/";
    }


    private static WorkflowDef getWorkflowDef() throws IOException {
        InputStream resourceAsStream = EndToEndTest.class.getClassLoader().getResourceAsStream(WORKFLOW_JSON_FILE);
        return new ObjectMapper().readValue(resourceAsStream, WorkflowDef.class);
    }

    private static GenericContainer getRedisContainer() {
        return new GenericContainer(DockerImageName.parse("redis:6.2.3-alpine"))
                .withExposedPorts(6379)
                .withNetwork(network)
                .withNetworkAliases("rs");
    }


    private static GenericContainer getConductorUIContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:ui"))
                .withEnv("WF_SERVER", "http://conductor-server:8080")
                .withExposedPorts(5000, 5000)
                .withNetwork(network);

    }

    private static GenericContainer getElasticSearchContainer() {
        return new GenericContainer(DockerImageName.parse("elasticsearch:6.8.15"))
                .withEnv("transport.host", "0.0.0.0")
                .withEnv("discovery.type", "single-node")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx1024m")
                .withExposedPorts(9200, 9300)
                .withNetwork(network)
                .withNetworkAliases("es");
    }

    //Not specifying the config properties file

    private static GenericContainer getConductorContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:server"))
                .withEnv("CONFIG_PROP", "config-local.properties")//this corresponds to https://github.com/Netflix/conductor/blob/f013a53b345b21e890790c8b7a316a34d992fc2e/docker/server/config/config-local.properties
                .withExposedPorts(8080)
                .withNetworkAliases("conductor-server")
                .withNetwork(network);
    }


}
