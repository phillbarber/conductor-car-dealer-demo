package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class SaveOrderWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "SaveOrder";
    }

    @Override
    public TaskResult execute(Task task) {
        System.out.println("Doing " + getTaskDefName());
        TaskResult result = new TaskResult(task);
        result.setStatus(COMPLETED);

        result.getOutputData().put("orderId", "abc25252");
        System.out.println("Doing " + getTaskDefName());
        return result;
    }
}