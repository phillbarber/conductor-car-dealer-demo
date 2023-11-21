package com.github.phillbarber.conductor.workers;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.Map;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;

public class CheckOrderIsValidWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "CheckOrderIsValid";
    }

    @Override
    public TaskResult execute(Task task) {
        Map<String, Object> inputData = task.getInputData();

        System.out.println("Doing " + getTaskDefName() + " input data is... ");
        System.out.println(inputData);
        System.out.println(inputData.get("order"));

        TaskResult result = new TaskResult(task);
        result.setStatus(COMPLETED);
        result.getOutputData().put("orderValid", true);

        return result;
    }
}