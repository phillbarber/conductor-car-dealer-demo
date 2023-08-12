import com.netflix.conductor.client.worker.Worker
import com.netflix.conductor.common.metadata.tasks.Task
import com.netflix.conductor.common.metadata.tasks.TaskResult

class CheckOrderIsValidWorker(val taskName: String) : Worker {

    override fun getTaskDefName(): String {
        return this.javaClass.name
    }

    override fun execute(task: Task?): TaskResult {
        val result = TaskResult(task)
        result.status = TaskResult.Status.COMPLETED

        result.outputData["isvalid"] = true

        return result
    }
}