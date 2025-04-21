package me.kpavlov.aimocks.a2a

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.CancelTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.PushNotificationConfig
import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskStreamingRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskResubscriptionRequest
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.notifications.NotificationListener
import me.kpavlov.aimocks.a2a.notifications.NotificationSender
import me.kpavlov.aimocks.a2a.notifications.TaskNotificationHistory
import me.kpavlov.aimocks.a2a.notifications.configureNotificationListener
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.mokksy.ServerConfiguration

private const val DEFAULT_NOTIFICATIONS_URI = "/notifications"

public open class MockAgentServer @JvmOverloads constructor(
    port: Int = 0,
    verbose: Boolean = false,
    public val notificationsUri: String = DEFAULT_NOTIFICATIONS_URI,
    private val notificationListener: NotificationListener = NotificationListener(notificationsUri),
) : AbstractMockLlm(
    port = port,
    configuration =
        ServerConfiguration(
            verbose = verbose,
        ) { config ->
            config.json(
                Json { ignoreUnknownKeys = true },
            )
        },
    applicationConfigurer = {
        this.configureNotificationListener(
            notificationsUri = notificationsUri,
            listener = notificationListener
        )
    }
) {

    public fun notificationUrl(): String {
        return baseUrl() + notificationsUri
    }

    private val notificationSender: NotificationSender = NotificationSender()

    /**
     * Configures a behavior for handling
     * ["Agent Card"](https://google.github.io/A2A/#/documentation?id=agent-card) mock server requests.
     * This method simulates interactions with an endpoint that retrieves agent card data.
     *
     * > Remote Agents that support A2A are required to publish an Agent Card in JSON format describing
     * > the agent’s capabilities/skills and authentication mechanism.
     * > Clients use the Agent Card information to identify the best agent that can perform
     * > a task and leverage A2A to communicate with that remote agent.
     *
     * Example usage:
     * ```kotlin
     * // Configure the mock server to respond with the AgentCard
     * a2aServer.agentCard() responds {
     *     delay = 1.milliseconds
     *     card {
     *        name = "test-agent"
     *        description = "test-agent-description"
     *        url = a2aServer.baseUrl()
     *        documentationUrl = "https://example.com/documentation"
     *        version = "0.0.1"
     *        provider = AgentProvider(
     *            "Acme, Inc.",
     *            "https://example.com/organization",
     *        )
     *        authentication = AgentAuthentication(
     *            schemes = listOf("none", "bearer"),
     *            credentials = "test-token",
     *        )
     *        capabilities = AgentCapabilities(
     *            streaming = true,
     *            pushNotifications = true,
     *            stateTransitionHistory = true,
     *        )
     *        skills = listOf(
     *            AgentSkill(
     *                id = "walk",
     *                name = "Walk the walk",
     *            ),
     *            AgentSkill(
     *                id = "talk",
     *                name = "Talk the talk",
     *            ),
     *        )
     *     }
     * }
     * ```
     *
     * @param name An optional identifier for the configured request.
     * @return An instance of `AbstractBuildingStep` allowing further configuration of the response
     *         for agent card requests using `AgentCardResponseSpecification`.
     */
    public fun agentCard(
        name: String? = null,
    ): AbstractBuildingStep<Nothing, AgentCardResponseSpecification> {
        val requestStep =
            mokksy.get(
                name = name,
                requestType = Nothing::class,
            ) {
                path("/.well-known/agent.json")
            }

        return AgentCardBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server
     * to handle ["Send a Task"](https://google.github.io/A2A/#/documentation?id=send-a-task) requests.
     *
     * This method simulates the behavior of the A2A's
     * ["Send a Task"](https://google.github.io/A2A/#/documentation?id=send-a-task) endpoint.
     * It allows defining how the server should respond to a "send task" JsonRPC request by chaining
     * a response configuration using the `responds` method of the returned `SendTaskBuildingStep`.
     *
     * Example usage:
     * ```kotlin
     * // Create a Task object
     * val task = Task(
     *     id = "tid_12345",
     *     sessionId = null,
     *     status = TaskStatus(state = "completed"),
     *     artifacts = listOf(
     *         Artifact(
     *             name = "joke",
     *             parts = listOf(
     *                 TextPart(
     *                     text = "This is a joke",
     *                 ),
     *             ),
     *         ),
     *     ),
     * )
     *
     * // Configure the mock server to respond with the task
     * a2aServer.sendTask() responds {
     *     id = 1
     *     result = task
     * }
     * ```
     *
     * @return An instance of `SendTaskBuildingStep` to define the response behavior for "send task" requests.
     */
    @JvmOverloads
    public fun sendTask(name: String? = null): SendTaskBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = SendTaskRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/send"
                    }
                }

        return SendTaskBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun cancelTask(name: String? = null): CancelTaskBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = CancelTaskRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/cancel"
                    }
                }

        return CancelTaskBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun sendTaskStreaming(name: String? = null): SendTaskStreamingBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = SendTaskStreamingRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/sendSubscribe"
                    }
                }

        return SendTaskStreamingBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server to handle
     * ["Get a Task"](https://google.github.io/A2A/#/documentation?id=get-a-task) requests.
     *
     * This method defines a mock server behavior by simulating the A2A's
     * ["Get a Task"](https://google.github.io/A2A/#/documentation?id=get-a-task) endpoint. It creates
     * a request specification that listens for the "tasks/get" JsonRPC method and includes specific configurations
     * for response behaviors that can be chained using the returned `GetTaskBuildingStep` instance.
     *
     * Example usage:
     * ```kotlin
     * // Configure the mock server to respond with a task
     * a2aServer.getTask() responds {
     *     id = 1
     *     result {
     *         id = "tid_12345"
     *         sessionId = null
     *         status = TaskStatus(state = "completed")
     *         artifacts = listOf(
     *             Artifact(
     *                 name = "joke",
     *                 parts = listOf(
     *                     TextPart(
     *                         text = "This is a joke",
     *                     ),
     *                 ),
     *             ),
     *         )
     *     }
     * }
     * ```
     *
     * @param name An optional identifier for the configured request. This can be used to distinguish between
     *             multiple mocked behaviors for "Get a Task" requests.
     * @return An instance of `GetTaskBuildingStep` that allows for further configuration of the mock
     *         response behavior for "Get a Task" requests.
     */
    @JvmOverloads
    public fun getTask(name: String? = null): GetTaskBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = GetTaskRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/get"
                    }
                }

        return GetTaskBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun getTaskPushNotification(name: String? = null): GetTaskPushNotificationBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = GetTaskPushNotificationRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/pushNotification/get"
                    }
                }

        return GetTaskPushNotificationBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun setTaskPushNotification(name: String? = null): SetTaskPushNotificationBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = SetTaskPushNotificationRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/pushNotification/set"
                    }
                }

        return SetTaskPushNotificationBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server to handle
     * ["Resubscribe to a Task"](https://google.github.io/A2A/#/documentation?id=resubscribe-to-a-task) requests.
     *
     * This method simulates the behavior of the A2A's
     * ["Resubscribe to a Task"](https://google.github.io/A2A/#/documentation?id=resubscribe-to-a-task) endpoint.
     * It allows defining how the server should respond to a "task resubscription" JsonRPC request by chaining
     * a response configuration using the `responds` method of the returned `TaskResubscriptionBuildingStep`.
     *
     * Example usage:
     * ```kotlin
     * // Configure the mock server to respond with task updates
     * a2aServer.taskResubscription() responds {
     *     responseFlow = flow {
     *         emit(
     *             taskStatusUpdateEvent {
     *                 id = "task_12345"
     *                 status {
     *                     state = "completed"
     *                     timestamp = System.now()
     *                 }
     *                 final = true
     *             }
     *         )
     *     }
     * }
     * ```
     *
     * @param name An optional identifier for the configured request.
     * @return An instance of `TaskResubscriptionBuildingStep` to define the response behavior
     * for task resubscription requests.
     */
    @JvmOverloads
    public fun taskResubscription(name: String? = null): TaskResubscriptionBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = TaskResubscriptionRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/resubscribe"
                    }
                }

        return TaskResubscriptionBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    public fun getTaskNotifications(taskId: TaskId): TaskNotificationHistory {
        return notificationListener.getByTaskId(taskId)
    }

    @JvmOverloads
    public suspend fun sendPushNotification(
        config: PushNotificationConfig = PushNotificationConfig(url = notificationUrl()),
        event: TaskUpdateEvent
    ) {
        notificationSender.sendPushNotification(config, event)
    }
}
