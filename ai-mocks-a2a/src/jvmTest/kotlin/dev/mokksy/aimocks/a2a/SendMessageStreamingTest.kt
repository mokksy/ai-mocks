package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.Message
import dev.mokksy.aimocks.a2a.model.MessageSendParams
import dev.mokksy.aimocks.a2a.model.SendStreamingMessageRequest
import dev.mokksy.aimocks.a2a.model.TaskArtifactUpdateEvent
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.TaskStatusUpdateEvent
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import dev.mokksy.aimocks.a2a.model.TextPart
import dev.mokksy.aimocks.a2a.model.taskArtifactUpdateEvent
import dev.mokksy.aimocks.a2a.model.taskStatusUpdateEvent
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.sse.sse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.time.Clock.System
import kotlin.time.Duration.Companion.seconds

internal class SendMessageStreamingTest : AbstractTest() {
    /**
     * https://github.com/google/A2A/blob/gh-pages/documentation.md#send-a-task
     */
    @OptIn(InternalAPI::class)
    @Test
    @Suppress("LongMethod")
    fun `Should send task streaming`() =
        runTest {
            val taskId: TaskId = "task_12345"

            a2aServer.sendMessageStreaming() responds {
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

            val collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()
            a2aClient.sse(
                request = {
                    url { a2aServer.baseUrl() }
                    method = HttpMethod.Post
                    val payload =
                        SendStreamingMessageRequest(
                            id = "1",
                            params =
                                MessageSendParams.create {
                                    message {
                                        role = Message.Role.user
                                        parts +=
                                            textPart {
                                                text = "Tell me a joke"
                                            }
                                    }
                                },
                        )
                    body =
                        TextContent(
                            text = Json.encodeToString(payload),
                            contentType = ContentType.Application.Json,
                        )
                },
            ) {
                var reading = true
                while (reading) {
                    incoming.collect { sse ->
                        logger.info { "Event from server:\n$sse" }
                        sse.data?.let {
                            val event = Json.decodeFromString<TaskUpdateEvent>(it)
                            collectedEvents.add(event)
                            if (!handleEvent(event)) {
                                reading = false
                                cancel("Finished")
                            }
                        }
                    }
                }
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
