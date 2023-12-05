package com.github.phillbarber.conductor.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.remoteservices.Order;
import com.github.phillbarber.conductor.remoteservices.OrderRemoteService;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class SaveOrderWorker implements Worker {


    private OrderRemoteService service;

    public SaveOrderWorker(OrderRemoteService service) {
        this.service = service;
    }

    @Override
    public String getTaskDefName() {
        return "SaveOrder";
    }

    @Override
    public TaskResult execute(Task task) {
        Order order = new ObjectMapper().convertValue(task.getInputData(), Order.class);
        System.out.println("Doing " + getTaskDefName());

        String orderId = service.saveOrder(order);
        System.out.println(order);
        TaskResult result = new TaskResult(task);
        result.setStatus(COMPLETED);

        result.getOutputData().put("order", order.withId(orderId));
        System.out.println("Doing " + getTaskDefName());
        return result;
    }
}