package com.github.phillbarber.conductor;

import com.github.phillbarber.conductor.workers.*;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;

import java.util.Arrays;
import java.util.List;

public class Launcher {

    public static void main(String[] args) {
        TaskClient taskClient = createClient();
        List<Worker> workers = getWorkers();

        startTaskRunner(taskClient, workers);
    }

    private static void startTaskRunner(TaskClient taskClient, List<Worker> workers) {
        TaskRunnerConfigurer configurer =
                new TaskRunnerConfigurer.Builder(taskClient, workers)
                        .withThreadCount(workers.size())
                        .build();
        configurer.init();
    }

    private static List<Worker> getWorkers() {
        List<Worker> workers = Arrays.asList(
                new CheckOrderIsValidWorker(),
                new GetBasePriceWorker(),
                new GetCustomerDetailsWorker(),
                new GetDiscountWorker(),
                new GetPriceForExtrasWorker(),
                new SaveOrderWorker());
        return workers;
    }

    private static TaskClient createClient() {
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI("http://localhost:8080/api/"); // Point this to the server API
        return taskClient;
    }

}
