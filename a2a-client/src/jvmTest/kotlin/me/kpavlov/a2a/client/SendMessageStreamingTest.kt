package me.kpavlov.a2a.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock.System
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.MessageSendParams
import me.kpavlov.aimocks.a2a.model.TaskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.TextPart
import me.kpavlov.aimocks.a2a.model.taskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.taskStatusUpdateEvent
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class SendMessageStreamingTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#72-messagestream
     */
    @OptIn(InternalAPI::class)
    @Test
    @Suppress("LongMethod")
    fun `Should send task streaming`() =
        runTest {
            val taskId: TaskId = "task_12345"

            a2aServer.sendMessageStreaming() responds {
                delayBetweenChunks = 500.milliseconds
                responseFlow =
                    flow {
                        emit(
                            taskStatusUpdateEvent {
                                id = taskId
                                status {
                                    state = "working"
                                    timestamp = System.now()
                                }
                            },
                        )
                        emit(
                            taskArtifactUpdateEvent {
                                id = taskId
                                artifact {
                                    name = "joke"
                                    parts +=
                                        textPart {
                                            text = "This"
                                        }
                                }
                            },
                        )
                        emit(
                            taskArtifactUpdateEvent {
                                id = taskId
                                artifact {
                                    name = "joke"
                                    parts +=
                                        textPart {
                                            text = "is"
                                        }
                                    append = true
                                }
                            },
                        )
                        emit(
                            taskArtifactUpdateEvent {
                                id = taskId
                                artifact {
                                    name = "joke"
                                    parts +=
                                        textPart {
                                            text = "a"
                                        }
                                    append = true
                                }
                            },
                        )
                        emit(
                            taskArtifactUpdateEvent {
                                id = taskId
                                artifact {
                                    name = "joke"
                                    parts +=
                                        textPart {
                                            text = "joke!"
                                        }
                                    append = true
                                    lastChunk = true
                                }
                            },
                        )
                        emit(
                            taskStatusUpdateEvent {
                                id = taskId
                                status {
                                    state = "completed"
                                    timestamp = System.now()
                                }
                                final = true
                            },
                        )
                    }
            }

            val taskParams =
                MessageSendParams.create {
                    message {
                        role = Message.Role.user
                        parts +=
                            textPart {
                                text = "Tell me a joke"
                            }
                    }
                }

            val collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()
            client.sendStreamingMessage(params = taskParams).collect { event ->
                logger.info { "Event from server: $event" }
                collectedEvents.add(event)
                handleEvent(event)
            }

            collectedEvents shouldHaveSize 6
            collectedEvents.forEach {
                it.id() shouldBe taskId
            }
            val firstEvent = collectedEvents.first() as TaskStatusUpdateEvent
            assertSoftly(firstEvent.status) {
                state shouldBe "working"
                timestamp.shouldNotBeNull()
            }
            val lastEvent = collectedEvents.last() as TaskStatusUpdateEvent
            assertSoftly(lastEvent) {
                final shouldBe true
                status.state shouldBe "completed"
                status.timestamp.shouldNotBeNull()
            }
            val joke =
                collectedEvents
                    .filter { it is TaskArtifactUpdateEvent }
                    .map { it as TaskArtifactUpdateEvent }
                    .filter { it.artifact.name == "joke" }
                    .map { it.artifact.parts[0] as TextPart }
                    .map { it.text }
                    .toList()
                    .joinToString(separator = " ")
            joke shouldBe "This is a joke!"
        }

    private fun handleEvent(event: TaskUpdateEvent): Boolean {
        when (event) {
            is TaskStatusUpdateEvent -> {
                logger.info { "Task status: $event" }
                if (event.final) {
                    return false
                }
            }

            is TaskArtifactUpdateEvent -> {
                logger.info { "Task artifact: $event" }
            }
        }
        return true
    }
}
