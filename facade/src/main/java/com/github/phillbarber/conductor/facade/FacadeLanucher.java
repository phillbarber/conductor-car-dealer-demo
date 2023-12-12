package com.github.phillbarber.conductor.facade;


import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


public class FacadeLanucher {

    public static final String BASE_URI = "http://localhost:8080/";

    private static final Logger logger = LoggerFactory.getLogger(FacadeLanucher.class);

    public static Server startServer() {

        final ResourceConfig config = new ResourceConfig(OrderResource.class);
        final Server server =
                JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);

        return server;

    }

    public static void main(String[] args) {
        try {

            final Server server = startServer();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down the application...");
                    server.stop();
                    System.out.println("Done, exit.");
                } catch (Exception e) {
                    logger.error("Error starting server", e);
                }
            }));

            System.out.println(
                    String.format("Application started.%nStop the application using CTRL+C"));

            // block and wait shut down signal, like CTRL+C
            Thread.currentThread().join();

        } catch (Exception ex) {
            logger.error("Some error", ex);
        }

    }
}