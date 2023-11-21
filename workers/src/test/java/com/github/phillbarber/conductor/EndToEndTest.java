package com.github.phillbarber.conductor;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;



@Testcontainers
public class EndToEndTest {

    public static final String WORKFLOW_JSON_FILE = "workflows/car-order-workflow.json";
    private Network network = Network.newNetwork();

    @Container
    private GenericContainer redis = getRedisContainer();

    @Container
    private GenericContainer elastic = getElasticSearchContainer();

    @Container
    private GenericContainer conductorServer = getConductorContainer();

    @Container
    private GenericContainer conductorUI = getConductorUIContainer();

    private Launcher launcher;

    @BeforeEach
    public void checkAllRunning() {

        assertTrue(redis.isRunning());
        assertTrue(conductorServer.isRunning());
        assertTrue(elastic.isRunning());
        assertTrue(conductorUI.isRunning());
    }

    @BeforeEach
    public void start(){
        this.launcher = startWorkers(getConductorServerURL());
    }
    @Test
    public void happyPathOrder() throws IOException {
        initialiseWorkflow();
        String workflowId = startWorkflow(getHappyPathInput());
        waitForWorkflowToFinish(workflowId);
        assertNotNull(getWorkflowClient().getWorkflow(workflowId, true).getOutput().get("orderId"));

    }

    @AfterEach
    public void stop(){
        launcher.shutdown();
    }

    private void waitForWorkflowToFinish(String workflowId) {
        await()
                .atLeast(Duration.of(1, ChronoUnit.SECONDS))
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .with()
                .pollInterval(Duration.of(1, ChronoUnit.SECONDS))
                .until(() -> getWorkflowClient().getWorkflow(workflowId, true).getStatus()== Workflow.WorkflowStatus.COMPLETED);
    }

    private static HashMap getHappyPathInput() throws IOException {
        return new ObjectMapper().readValue("""
                {
                  "order" : {
                    "car" : {
                      "make": "Blista",
                      "model": "Compact",
                      "extras" : null
                    },
                    "customer" :{
                      "id" : "12345"
                    }
                  }
                }
                """, HashMap.class);
    }

    private Launcher startWorkers(String conductorServerURL) {
        Launcher launcher = new Launcher(conductorServerURL);
        new Thread(launcher::start).start();
        return launcher;
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

    private void initialiseWorkflow() throws IOException {
        MetadataClient metadataClient = new MetadataClient();
        metadataClient.setRootURI(getConductorServerURL());
        metadataClient.registerWorkflowDef(getWorkflowDef());
    }

    @NotNull
    private String getConductorServerURL() {
        return "http://localhost:" + conductorServer.getMappedPort(8080) + "/api/";
    }


    private WorkflowDef getWorkflowDef() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(WORKFLOW_JSON_FILE);
        return new ObjectMapper().readValue(resourceAsStream, WorkflowDef.class);
    }

    private GenericContainer getRedisContainer() {
        return new GenericContainer(DockerImageName.parse("redis:6.2.3-alpine"))
                .withExposedPorts(6379)
                .withNetwork(network)
                .withNetworkAliases("rs");
    }


    private GenericContainer getConductorUIContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:ui"))
                .withEnv("WF_SERVER", "http://conductor-server:8080")
                .withExposedPorts(5000, 5000)
                .withNetwork(network);

    }

    private GenericContainer getElasticSearchContainer() {
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

    private GenericContainer getConductorContainer() {
        return new GenericContainer(DockerImageName.parse("conductor:server"))
                .withEnv("CONFIG_PROP", "config-local.properties")//this corresponds to https://github.com/Netflix/conductor/blob/f013a53b345b21e890790c8b7a316a34d992fc2e/docker/server/config/config-local.properties
                .withExposedPorts(8080)
                .withNetworkAliases("conductor-server")
                .withNetwork(network);
    }


}
