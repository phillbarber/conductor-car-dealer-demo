package com.github.phillbarber.conductor.facade;


import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;

import java.net.URI;


public class FacadeLanucher {

    public static final String BASE_URI = "http://localhost:8080";

    private static final Logger logger = LoggerFactory.getLogger(FacadeLanucher.class);

    public static void main(String[] args) {
        //javax.xml.bind.JAXBContext.class

        startServer();

    }

    public static void startServer() {
        try {

            final ResourceConfig config = new ClassNamesResourceConfig(OrderResource.class);
            final HttpServer server =  GrizzlyServerFactory.createHttpServer(BASE_URI, config);

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