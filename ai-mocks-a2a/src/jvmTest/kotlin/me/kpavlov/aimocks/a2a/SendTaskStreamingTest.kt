package me.kpavlov.aimocks.a2a

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.sse.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock.System
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.Artifact
import me.kpavlov.aimocks.a2a.model.Message
import me.kpavlov.aimocks.a2a.model.SendTaskStreamingRequest
import me.kpavlov.aimocks.a2a.model.TaskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskStatus
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.TextPart
import me.kpavlov.aimocks.a2a.model.create
import java.util.*
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
                            TaskStatusUpdateEvent(
                                id = taskId,
                                status = TaskStatus(state = "working", timestamp = System.now()),
                            ),
                        )
                        emit(
                            TaskArtifactUpdateEvent.create {
                                id = taskId
                                artifact =
                                    Artifact(
                                        name = "joke",
                                        parts =
                                            listOf(
                                                TextPart(
                                                    text = "This",
                                                ),
                                            ),
                                        append = false,
                                    )
                            },
                        )
                        emit(
                            TaskArtifactUpdateEvent.create {
                                id = taskId
                                artifact =
                                    Artifact(
                                        name = "joke",
                                        parts =
                                            listOf(
                                                TextPart(
                                                    text = "is",
                                                ),
                                            ),
                                        append = false,
                                    )
                            },
                        )
                        emit(
                            TaskArtifactUpdateEvent.create {
                                id = taskId
                                artifact =
                                    Artifact(
                                        name = "joke",
                                        parts =
                                            listOf(
                                                TextPart(
                                                    text = "a",
                                                ),
                                            ),
                                        append = false,
                                    )
                            },
                        )
                        emit(
                            TaskArtifactUpdateEvent.create {
                                id = taskId
                                artifact =
                                    Artifact(
                                        name = "joke",
                                        parts =
                                            listOf(
                                                TextPart(
                                                    text = "joke!",
                                                ),
                                            ),
                                        append = false,
                                        lastChunk = true,
                                    )
                            },
                        )
                        emit(
                            TaskStatusUpdateEvent(
                                id = taskId,
                                status = TaskStatus(state = "completed", timestamp = System.now()),
                                final = true,
                            ),
                        )
                    }
            }

            var collectedEvents = ConcurrentLinkedQueue<TaskUpdateEvent>()
            a2aClient.sse(
                request = {
                    url { a2aServer.baseUrl() }
                    method = HttpMethod.Post
                    val payload =
                        SendTaskStreamingRequest(
                            id = "1",
                            params =
                                TaskSendParams.create {
                                    id = UUID.randomUUID().toString()
                                    message =
                                        Message(
                                            role = Message.Role.user,
                                            parts =
                                                listOf(
                                                    TextPart(
                                                        text = "Tell me a joke",
                                                    ),
                                                ),
                                        )
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
                    incoming.collect {
                        logger.info { "Event from server:\n$it" }
                        it.data?.let {
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
