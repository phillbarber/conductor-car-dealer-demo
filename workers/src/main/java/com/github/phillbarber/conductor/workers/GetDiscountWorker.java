package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class GetDiscountWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "GetDiscount";
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        result.getOutputData().put("discount", 0.1);
        result.getOutputData().put("promotionCode", "ABCDE1234");
        result.getOutputData().put("totalPrice", 54000);
        result.setStatus(COMPLETED);
        return result;
    }
}