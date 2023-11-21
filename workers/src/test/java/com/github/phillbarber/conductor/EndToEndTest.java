package com.github.phillbarber.conductor;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

    @Test
    public void stuff() throws InterruptedException, IOException {
        assertTrue(redis.isRunning());
        assertTrue(conductorServer.isRunning());
        assertTrue(elastic.isRunning());
        assertTrue(conductorUI.isRunning());


        initialiseWorkflow();

        startWorkers(getConductorServerURL());

        String workflowId = startWorkflow();

        for (int i =0 ; i<10 ; i++){
            Workflow workflow = getWorkflowClient().getWorkflow(workflowId, true);
            if (workflow.getStatus() == Workflow.WorkflowStatus.COMPLETED){
                Object orderId = workflow.getOutput().get("orderId");
                System.out.println("The order id is " + orderId);
            }
            System.out.println("Trying " + i);
            System.out.println(workflow);
            Thread.sleep(1000);
        }

        Thread.sleep(1000000);

    }

    private void startWorkers(String conductorServerURL) {
        new Thread(() -> Launcher.main(new String[]{conductorServerURL})).start();
    }

    private String startWorkflow() {
        WorkflowClient workflowClient = getWorkflowClient();
        return workflowClient.startWorkflow(getStartWorkflowRequest());
    }

    @NotNull
    private WorkflowClient getWorkflowClient() {
        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(getConductorServerURL());
        return workflowClient;
    }

    @NotNull
    private static StartWorkflowRequest getStartWorkflowRequest() {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName("CarOrderWorkflow");
        startWorkflowRequest.setInput(new HashMap<>());
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
