package com.github.phillbarber.conductor.facade;



import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.run.Workflow;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.awaitility.Awaitility.await;

@Path("/order")
public class OrderResource {


    private final Map mapOfStuff;
    private final WorkflowClient workflowClient;

    public OrderResource(String message, WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
        mapOfStuff = new HashMap();
        mapOfStuff.put("message", message);

        System.out.println("NOICE");
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map createOrder() {
        return mapOfStuff;

    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map createOrder(Map input) {

        StartWorkflowRequest startWorkflowRequest = getStartWorkflowRequest(input);
        String workflowId = workflowClient.startWorkflow(startWorkflowRequest);
        waitForWorkflowToFinish(workflowId);
        Workflow workflow = workflowClient.getWorkflow(workflowId, true);

        System.out.println("YESS");
        return workflow.getOutput();//this is a hack

    }


    private void waitForWorkflowToFinish(String workflowId) {
        await()
                .atLeast(Duration.of(1, ChronoUnit.SECONDS))
                .atMost(Duration.of(10, ChronoUnit.MINUTES))
                .with()
                .pollInterval(Duration.of(1, ChronoUnit.SECONDS))
                .until(() -> workflowClient.getWorkflow(workflowId, true).getStatus() == Workflow.WorkflowStatus.COMPLETED);
    }


    private static StartWorkflowRequest getStartWorkflowRequest(Map input) {
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName("CarOrderWorkflow");
        startWorkflowRequest.setInput(input);
        return startWorkflowRequest;
    }

}