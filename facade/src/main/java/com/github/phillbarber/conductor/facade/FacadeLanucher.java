package com.github.phillbarber.conductor.facade;


import com.netflix.conductor.client.http.WorkflowClient;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;

import java.io.IOException;


public class FacadeLanucher {

    private final  String restFacadeURL;
    private final String conductorServerURL;

    private final HttpServer server;

    private static final Logger logger = LoggerFactory.getLogger(FacadeLanucher.class);

    public FacadeLanucher(String restFacadeURL, String conductorServerURL) {
        this.restFacadeURL = restFacadeURL;
        this.conductorServerURL = conductorServerURL;
        try {
            server =  GrizzlyServerFactory.createHttpServer(restFacadeURL, createResourceConfig(conductorServerURL));
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        try {
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

    public boolean isRunning(){
        return server.isStarted();
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