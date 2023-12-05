package com.github.phillbarber.conductor.workers;

import com.github.phillbarber.conductor.OrderRequest;
import com.github.phillbarber.conductor.remoteservices.OrderRemoteService;
import com.github.phillbarber.conductor.remoteservices.OrderValidationResponse;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckOrderIsValidWorker implements Worker {

    private OrderRemoteService service;

    public CheckOrderIsValidWorker(OrderRemoteService service) {
        this.service = service;
    }

    @Override
    public String getTaskDefName() {
        return "CheckOrderIsValid";
    }

    @Override
    public TaskResult execute(Task task) {

        OrderRequest orderRequest = new ObjectMapper().convertValue(task.getInputData().get("order"), OrderRequest.class);

        TaskResult result = new TaskResult(task);

        OrderValidationResponse validationResponse = service.getValidationResponse(orderRequest);

        if (validationResponse.isValid()){
            result.getOutputData().put("orderValid", true);
        }
        else{
            result.getOutputData().put("orderValid", false);
            result.getOutputData().put("rejection", "We don't sell Sentinels");
        }
        result.setStatus(COMPLETED);
        return result;
    }



}