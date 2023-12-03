package com.github.phillbarber.conductor.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.Order;
import com.github.phillbarber.conductor.remoteservices.BasePriceRemoteService;
import com.github.phillbarber.conductor.remoteservices.BasePriceResponse;
import com.github.phillbarber.conductor.remoteservices.OrderValidationResponse;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetBasePriceWorker implements Worker {

    private BasePriceRemoteService basePriceRemoteService;

    public GetBasePriceWorker(BasePriceRemoteService basePriceRemoteService) {
        this.basePriceRemoteService = basePriceRemoteService;
    }

    @Override
    public String getTaskDefName() {
        return "GetBasePrice";
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        Order order = new ObjectMapper().convertValue(task.getInputData().get("order"), Order.class);
        BasePriceResponse basePrice = basePriceRemoteService.getBasePrice(order);
        result.getOutputData().put("basePrice", basePrice.basePrice());
        result.getOutputData().put("currency", basePrice.currency());
        result.setStatus(COMPLETED);
        return result;
    }
}