package com.github.phillbarber.conductor.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.OrderRequest;
import com.github.phillbarber.conductor.remoteservices.CustomerRemoteService;
import com.github.phillbarber.conductor.remoteservices.CustomerResponse;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetCustomerDetailsWorker implements Worker {

    private CustomerRemoteService customerService;

    public GetCustomerDetailsWorker(CustomerRemoteService customerService) {
        this.customerService = customerService;
    }

    @Override
    public String getTaskDefName() {
        return "GetCustomerDetails";
    }

    @Override
    public TaskResult execute(Task task) {
        OrderRequest orderRequest = new ObjectMapper().convertValue(task.getInputData().get("order"), OrderRequest.class);
        TaskResult result = new TaskResult(task);
        CustomerResponse customer = customerService.getCustomer(orderRequest.customer().id());
        result.getOutputData().put("customerName", customer.name());
        result.getOutputData().put("customerId", orderRequest.customer().id());
        result.getOutputData().put("customerLoyaltyPoints", customer.loyaltyPoints());
        result.setStatus(COMPLETED);
        return result;
    }
}