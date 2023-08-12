package com.github.phillbarber.conductor.order;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.HashMap;

public class CheckOrderIsValidWorker implements Worker {

    private String taskName;

    public CheckOrderIsValidWorker(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getTaskDefName() {
        return taskName;
    }

    public TaskResult execute(Task task)  {
        TaskResult result = new TaskResult(task);
        result.setStatus(TaskResult.Status.COMPLETED);

        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("isValid", true);
        result.setOutputData(objectObjectHashMap);

        return result;
    }
}