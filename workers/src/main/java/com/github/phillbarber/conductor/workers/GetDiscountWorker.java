package com.github.phillbarber.conductor.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.phillbarber.conductor.OrderRequest;
import com.github.phillbarber.conductor.remoteservices.DiscountPriceRemoteService;
import com.github.phillbarber.conductor.remoteservices.DiscountPriceResponse;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetDiscountWorker implements Worker {

    private DiscountPriceRemoteService discountService;

    public GetDiscountWorker(DiscountPriceRemoteService discountService) {
        this.discountService = discountService;
    }

    @Override
    public String getTaskDefName() {
        return "GetDiscount";
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        OrderRequest orderRequest = new ObjectMapper().convertValue(task.getInputData().get("order"), OrderRequest.class);
        Integer basePrice = new ObjectMapper().convertValue(task.getInputData().get("basePrice"), Integer.class);
        Integer customerLoyaltyPoints = new ObjectMapper().convertValue(task.getInputData().get("customerLoyaltyPoints"), Integer.class);

        DiscountPriceResponse discountPrice = discountService.getDiscountPrice(orderRequest, basePrice, customerLoyaltyPoints);


        result.getOutputData().put("discount", discountPrice.discount());
        result.getOutputData().put("promotionCode", discountPrice.promotionCode());
        result.getOutputData().put("totalPrice", discountPrice.totalPrice());
        result.setStatus(COMPLETED);
        return result;
    }
}