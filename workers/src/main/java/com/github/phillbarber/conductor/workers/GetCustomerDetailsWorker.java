package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetCustomerDetailsWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "GetCustomerDetails";
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        result.getOutputData().put("customerName", "Marty McFly");
        result.getOutputData().put("customerId", "12345");
        result.getOutputData().put("customerLoyaltyPoints", 12);
        result.setStatus(COMPLETED);
        return result;
    }
}