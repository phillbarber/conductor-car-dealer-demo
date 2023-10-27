package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

public class GetBasePriceWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return this.getClass().getName();
    }

    @Override
    public TaskResult execute(Task task) {
        System.out.println("Doing " + getTaskDefName());
        return null;
    }
}