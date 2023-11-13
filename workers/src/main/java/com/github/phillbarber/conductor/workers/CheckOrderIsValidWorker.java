package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class CheckOrderIsValidWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "CheckOrderIsValid";
    }

    @Override
    public TaskResult execute(Task task) {
        System.out.println("Doing " + getTaskDefName());
        TaskResult result = new TaskResult(task);
        result.setStatus(COMPLETED);

        //Register the output of the task
        result.getOutputData().put("foo", "bar");
        result.getOutputData().put("baz", "boz");
        result.getOutputData().put("number", 4);

        return new TaskResult(task);
    }
}