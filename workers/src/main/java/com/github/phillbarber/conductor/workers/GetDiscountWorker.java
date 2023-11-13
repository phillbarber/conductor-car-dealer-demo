package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

public class GetDiscountWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "GetDiscount";
    }

    @Override
    public TaskResult execute(Task task) {
        System.out.println("Doing " + getTaskDefName());
        return null;
    }
}