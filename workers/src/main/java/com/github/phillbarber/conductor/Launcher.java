package com.github.phillbarber.conductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.remoteservices.BasePriceRemoteService;
import com.github.phillbarber.conductor.remoteservices.CustomerRemoteService;
import com.github.phillbarber.conductor.remoteservices.DiscountPriceRemoteService;
import com.github.phillbarber.conductor.remoteservices.OrderRemoteService;
import com.github.phillbarber.conductor.workers.*;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import java.util.Arrays;
import java.util.List;

public class Launcher {

    private final TaskClient taskClient;
    private final List<Worker> workers;
    private final TaskRunnerConfigurer taskRunnerConfigurer;

    private final String serviceRootURI;

    public Launcher(String conductorRootURI, String serviceRootURI) {
        this.taskClient = createClient(conductorRootURI);
        this.serviceRootURI = serviceRootURI;
        this.workers = getWorkers();
        this.taskRunnerConfigurer = startTaskRunner(taskClient, workers);


    }
    public void start(){
        taskRunnerConfigurer.init();
    }

    public void shutdown(){
        taskRunnerConfigurer.shutdown();
    }

    /*
        Idea for workflow

        1. Check Order is Valid - checkOrder - returns validBoolean and message
        2. Get Customer Details - getCustomer - returns customerName and loyaltyPoints
        3. Get Base Price       - getBasePrice - returns price for car and customer
        4. Get Extra Price      - getPriceForExtras - takes car and extras
        5. Discount Service     - getDiscount - returns an amount less
        6. Save Order           - saveOrder   - saves order to DB
        Returns Valid order with price

        input:

        order
         car
          make
          model
          extras

        customer
         id


         returns:

          order
           id
           price
            basePrice
            extraPrice
            saving
           car
            make
            model
            extras



         */


    private TaskRunnerConfigurer startTaskRunner(TaskClient taskClient, List<Worker> workers) {
        TaskRunnerConfigurer configurer =
                new TaskRunnerConfigurer.Builder(taskClient, workers)
                        .withThreadCount(workers.size())
                        .build();

        return configurer;


    }



    private List<Worker> getWorkers() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(
                new CheckOrderIsValidWorker(new OrderRemoteService(httpClient, serviceRootURI, objectMapper)),
                new GetBasePriceWorker(new BasePriceRemoteService(httpClient, serviceRootURI, objectMapper)),
                new GetCustomerDetailsWorker(new CustomerRemoteService(httpClient, serviceRootURI, objectMapper)),
                new GetDiscountWorker(new DiscountPriceRemoteService(httpClient, serviceRootURI, objectMapper)),
                new SaveOrderWorker());
    }


    private TaskClient createClient(String rootURI) {
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI(rootURI); // This needs to get the port dynamically
        return taskClient;
    }

}
