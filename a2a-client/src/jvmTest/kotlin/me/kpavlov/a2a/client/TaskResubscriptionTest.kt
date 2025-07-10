package me.kpavlov.a2a.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock.System
import me.kpavlov.aimocks.a2a.model.TaskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.TextPart
import me.kpavlov.aimocks.a2a.model.taskArtifactUpdateEvent
import me.kpavlov.aimocks.a2a.model.taskStatusUpdateEvent
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class TaskResubscriptionTest : AbstractTest() {
    /**
     * Test for task resubscription operation
     */
    @OptIn(InternalAPI::class)
    @Test
    @Suppress("LongMethod")
    fun `Should resubscribe to task`() =
        runTest {
            val taskId: TaskId = "task_12345"

            a2aServer.taskResubscription() responds {
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
                                            text = "This is a resubscribed joke!"
                                        }
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
            client.resubscribeToTask(taskId).collect { event ->
                logger.info { "Event from server: $event" }
                collectedEvents.add(event)
                handleEvent(event)
            }

            collectedEvents shouldHaveSize 3
            collectedEvents.forEach {
                it.id() shouldBe taskId
            }
            val firstEvent = collectedEvents.first() as TaskStatusUpdateEvent
            assertSoftly(firstEvent) {
                status.state shouldBe "working"
            }
            val lastEvent = collectedEvents.last() as TaskStatusUpdateEvent
            assertSoftly(lastEvent) {
                final shouldBe true
                status.state shouldBe "completed"
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
            joke shouldBe "This is a resubscribed joke!"
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
