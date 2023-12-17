package com.github.phillbarber.conductor.facade;


import com.netflix.conductor.client.http.WorkflowClient;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;


public class FacadeLanucher {

    public static final String BASE_URI = "http://localhost:8080";

    private static final Logger logger = LoggerFactory.getLogger(FacadeLanucher.class);

    public static void main(String[] args) {
        startServer(null);
    }

    public static void startServer(String conductorServerURL) {
        try {

            final HttpServer server =  GrizzlyServerFactory.createHttpServer(BASE_URI, createResourceConfig(conductorServerURL));
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Shutting down the application...");
                    server.stop();
                    logger.info("Done, exit.");
                } catch (Exception e) {
                    logger.error("Error starting server", e);
                }
            }));

            logger.info(
                    String.format("Application started.%nStop the application using CTRL+C"));

        } catch (Exception ex) {
            logger.error("Some error", ex);
        }
    }

    private static ResourceConfig createResourceConfig(String conductorServerURL) {
        final ResourceConfig config = new DefaultResourceConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        config.getExplicitRootResources().put("order",new OrderResource("Secret Message", getWorkflowClient(conductorServerURL)));
        return config;
    }

    private static WorkflowClient getWorkflowClient(String conductorServerURL) {
        WorkflowClient workflowClient = new WorkflowClient();
        workflowClient.setRootURI(conductorServerURL);
        return workflowClient;
    }
}