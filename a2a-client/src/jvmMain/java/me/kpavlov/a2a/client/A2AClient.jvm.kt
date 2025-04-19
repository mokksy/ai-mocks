package me.kpavlov.a2a.client

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.concurrent.Executor
import java.util.concurrent.Flow
import java.util.concurrent.SubmissionPublisher

@DelicateCoroutinesApi
@JvmOverloads
public fun A2AClient.sendTaskStreamingAsJavaFlow(
    params: TaskSendParams,
    executor: Executor,
    maxBufferCapacity: Int = 64,
): Flow.Publisher<TaskUpdateEvent> {
    val client = this
    val publisher = SubmissionPublisher<TaskUpdateEvent>(executor, maxBufferCapacity)
    kotlinx.coroutines.GlobalScope.launch {
        client
            .sendTaskStreaming(params)
            .onEach { publisher.submit(it) }
            .onCompletion { cause: Throwable? ->
                if (cause != null) {
                    publisher.closeExceptionally(cause)
                } else {
                    publisher.close()
                }
            }
            .count()
    }
    return publisher
}
