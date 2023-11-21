package com.github.phillbarber.conductor;

import com.github.phillbarber.conductor.workers.*;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import java.util.Arrays;
import java.util.List;

public class Launcher {


    private final String rootURI;
    private final TaskClient taskClient;
    private final List<Worker> workers;
    private final TaskRunnerConfigurer taskRunnerConfigurer;

    public Launcher(String rootURI) {
        this.rootURI = rootURI;
        this.taskClient = createClient(rootURI);
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
        List<Worker> workers = Arrays.asList(
                new CheckOrderIsValidWorker(),
                new GetBasePriceWorker(),
                new GetCustomerDetailsWorker(),
                new GetDiscountWorker(),
                new GetPriceForExtrasWorker(),
                new SaveOrderWorker());
        return workers;
    }

    private TaskClient createClient(String rootURI) {
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI(rootURI); // This needs to get the port dynamically
        return taskClient;
    }

}
