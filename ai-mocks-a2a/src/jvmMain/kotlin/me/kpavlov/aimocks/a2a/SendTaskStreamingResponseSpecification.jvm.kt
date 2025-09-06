package me.kpavlov.aimocks.a2a

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.concurrent.Flow

public fun SendStreamingMessageResponseSpecification.publisher(publisher: Flow.Publisher<TaskUpdateEvent>) {
    responseFlow =
        flow {
            publisher.subscribe(
                object : Flow.Subscriber<TaskUpdateEvent> {
                    override fun onSubscribe(subscription: Flow.Subscription) {
                        println("onSubscribe")
                    }

                    override fun onNext(item: TaskUpdateEvent) {
                        println("onNext")
                        runBlocking {
                            emit(item)
                        }
                    }

                    override fun onError(throwable: Throwable) {
                        println("onError: $throwable")
                        throw throwable
                    }

                    override fun onComplete() {
                        println("onComplete")
                    }
                },
            )
        }
}
