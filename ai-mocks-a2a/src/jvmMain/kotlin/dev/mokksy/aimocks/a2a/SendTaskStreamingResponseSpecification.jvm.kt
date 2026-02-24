package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Flow

public fun SendStreamingMessageResponseSpecification.publisher(
    publisher: Flow.Publisher<TaskUpdateEvent>,
) {
    responseFlow =
        callbackFlow {
            publisher.subscribe(
                object : Flow.Subscriber<TaskUpdateEvent> {
                    override fun onSubscribe(subscription: Flow.Subscription) {
                        subscription.request(Long.MAX_VALUE)
                    }

                    override fun onNext(item: TaskUpdateEvent) {
                        trySend(item)
                    }

                    override fun onError(throwable: Throwable) {
                        close(throwable)
                    }

                    override fun onComplete() {
                        close()
                    }
                },
            )
            awaitClose()
        }
}
