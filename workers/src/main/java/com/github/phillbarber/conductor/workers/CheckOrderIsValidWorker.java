package com.github.phillbarber.conductor.workers;

import com.github.phillbarber.conductor.Order;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.Map;

import static com.netflix.conductor.common.metadata.tasks.TaskResult.Status.COMPLETED;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckOrderIsValidWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "CheckOrderIsValid";
    }

    @Override
    public TaskResult execute(Task task) {


        Order order = new ObjectMapper().convertValue(task.getInputData().get("order"), Order.class);

        TaskResult result = new TaskResult(task);

        if (order.car().make().equals("Sentinel")){
            result.getOutputData().put("orderValid", false);
            result.getOutputData().put("rejection", "We don't sell Sentinels");
        }
        else{
            result.getOutputData().put("orderValid", true);
        }

        result.setStatus(COMPLETED);



        return result;
    }



}