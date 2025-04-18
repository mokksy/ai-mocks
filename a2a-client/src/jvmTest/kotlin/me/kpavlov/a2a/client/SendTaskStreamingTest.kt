package me.kpavlov.a2a.client

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock.System
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.TaskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.TextPart
import me.kpavlov.aimocks.a2a.model.create
import me.kpavlov.aimocks.a2a.model.taskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.taskStatusUpdateEvent
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class SendTaskStreamingTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @OptIn(InternalAPI::class)
    @Test
    @Suppress("LongMethod")
    fun `Should send task streaming`() =
        runTest {
            val taskId: TaskId = "task_12345"

            a2aServer.sendTaskStreaming() responds {
                delayBetweenChunks = 1.seconds
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

            val taskParams = TaskSendParams.create {
                id = UUID.randomUUID().toString()
                message {
                    role = Message.Role.user
                    parts +=
                        textPart {
                            text = "Tell me a joke"
                        }
                }
            }

            val collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()
            client.sendTaskStreaming(taskParams).collect { event ->
                logger.info { "Event from server: $event" }
                collectedEvents.add(event)
                handleEvent(event)
            }

            collectedEvents shouldHaveSize 6
            collectedEvents.forEach {
                it.id() shouldBe taskId
            }
            (collectedEvents.first() as TaskStatusUpdateEvent).let {
                it.status.state shouldBe "working"
                it.status.timestamp.shouldNotBeNull()
            }
            (collectedEvents.last() as TaskStatusUpdateEvent).let {
                it.final shouldBe true
                it.status.state shouldBe "completed"
                it.status.timestamp.shouldNotBeNull()
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
