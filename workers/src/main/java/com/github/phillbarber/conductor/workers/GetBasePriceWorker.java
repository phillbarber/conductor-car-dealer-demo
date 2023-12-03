package com.github.phillbarber.conductor.workers;

import com.github.phillbarber.conductor.remoteservices.OrderValidationResponse;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetBasePriceWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "GetBasePrice";
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        result.getOutputData().put("basePrice", 60000);
        result.getOutputData().put("currency", "GBP");
        result.setStatus(COMPLETED);
        return result;
    }
}