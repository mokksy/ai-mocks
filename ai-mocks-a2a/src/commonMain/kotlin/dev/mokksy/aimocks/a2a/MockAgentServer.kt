package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.CancelTaskRequest
import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.GetAuthenticatedExtendedCardRequest
import dev.mokksy.aimocks.a2a.model.GetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.GetTaskRequest
import dev.mokksy.aimocks.a2a.model.ListTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.PushNotificationConfig
import dev.mokksy.aimocks.a2a.model.SendMessageRequest
import dev.mokksy.aimocks.a2a.model.SendStreamingMessageRequest
import dev.mokksy.aimocks.a2a.model.SetTaskPushNotificationRequest
import dev.mokksy.aimocks.a2a.model.TaskId
import dev.mokksy.aimocks.a2a.model.TaskResubscriptionRequest
import dev.mokksy.aimocks.a2a.model.TaskUpdateEvent
import dev.mokksy.aimocks.a2a.notifications.NotificationListener
import dev.mokksy.aimocks.a2a.notifications.NotificationSender
import dev.mokksy.aimocks.a2a.notifications.TaskNotificationHistory
import dev.mokksy.aimocks.a2a.notifications.configureNotificationListener
import dev.mokksy.aimocks.core.AbstractBuildingStep
import dev.mokksy.aimocks.core.AbstractMockLlm
import dev.mokksy.mokksy.ServerConfiguration
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val DEFAULT_NOTIFICATIONS_URI = "/notifications"

@Suppress("TooManyFunctions")
public open class MockAgentServer private constructor(
    port: Int,
    verbose: Boolean = false,
    public val notificationsUri: String,
    private val notificationListener: NotificationListener,
    private val notificationSender: NotificationSender,
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
            configureNotificationListener(
                notificationsUri = notificationsUri,
                listener = notificationListener,
                verbose = verbose,
            )
        },
    ) {
    /**
     * Constructor for initializing a `MockAgentServer` instance.
     *
     * @param port The port on which the mock server will run.
     *          Defaults to 0, which allows automatic port selection.
     * @param verbose Determines whether the server operates in verbose mode for detailed logging.
     *          Defaults to `false`.
     * @param notificationsUri The URI used for receiving notifications.
     *          Defaults to `DEFAULT_NOTIFICATIONS_URI`.
     */
    @JvmOverloads
    public constructor(
        port: Int = 0,
        verbose: Boolean = false,
        notificationsUri: String = DEFAULT_NOTIFICATIONS_URI,
    ) : this(
        port = port,
        verbose = verbose,
        notificationsUri = notificationsUri,
        notificationListener = NotificationListener(notificationsUri),
        notificationSender = NotificationSender(),
    )

    public fun notificationUrl(): String = baseUrl() + notificationsUri

    /**
     * Configures a behavior for handling
     * [Agent Card](https://a2a-protocol.org/latest/specification/) mock server requests.
     * This method simulates interactions with an endpoint that retrieves agent card data.
     *
     * > Remote Agents that support A2A are required to publish an Agent Card in JSON format describing
     * > the agent's capabilities/skills and authentication mechanism.
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
     * @see [A2A Protocol - Agent Card](https://a2a-protocol.org/latest/specification/)
     */
    public fun agentCard(
        name: String? = null,
    ): AbstractBuildingStep<Nothing, AgentCardResponseSpecification> {
        val requestStep =
            mokksy.get(
                name = name,
                requestType = Nothing::class,
            ) {
                path("/.well-known/agent-card.json")
            }

        return AgentCardBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server
     * to handle [message/send](https://a2a-protocol.org/latest/specification/) requests.
     *
     * This method simulates the behavior of the A2A protocol's message/send endpoint.
     * It allows defining how the server should respond to a "send message" JSON-RPC request by chaining
     * a response configuration using the `responds` method of the returned `SendTaskBuildingStep`.
     *
     * @deprecated Use [sendMessage] instead, which better reflects the A2A protocol naming.
     * This method is kept for backward compatibility and delegates to the same implementation.
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
     * a2aServer.sendMessage() responds {
     *     id = 1
     *     result = task
     * }
     * ```
     *
     * @return An instance of `SendTaskBuildingStep` to define the response behavior for message/send requests.
     * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
     */
    @JvmOverloads
    public fun sendMessage(name: String? = null): SendMessageBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = SendMessageRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "message/send"
                    }
                }

        return SendMessageBuildingStep(
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
    public fun sendMessageStreaming(name: String? = null): SendStreamingMessageBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = SendStreamingMessageRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "message/stream"
                    }
                }

        return SendStreamingMessageBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server
     * to handle [message/stream](https://a2a-protocol.org/latest/specification/) requests.
     *
     * This method simulates the behavior of the A2A protocol's message/stream endpoint,
     * which allows sending messages with real-time streaming responses via Server-Sent Events (SSE).
     * It allows defining how the server should respond to streaming message requests by chaining
     * a response configuration using the `responds` method of the returned `SendTaskStreamingBuildingStep`.
     *
     * Example usage:
     * ```kotlin
     * // Configure the mock server to respond with streaming task updates
     * a2aServer.sendStreamingMessage() responds {
     *     responseFlow = flow {
     *         emit(
     *             taskStatusUpdateEvent {
     *                 id = "task_12345"
     *                 status {
     *                     state = "working"
     *                     timestamp = System.currentTimeMillis()
     *                 }
     *                 final = false
     *             }
     *         )
     *         delay(1000)
     *         emit(
     *             taskStatusUpdateEvent {
     *                 id = "task_12345"
     *                 status {
     *                     state = "completed"
     *                     timestamp = System.currentTimeMillis()
     *                 }
     *                 final = true
     *             }
     *         )
     *     }
     * }
     * ```
     *
     * @param name An optional identifier for the configured request.
     * @return An instance of `SendTaskStreamingBuildingStep` to define the response behavior
     * for message/stream requests.
     * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
     */
    @JvmOverloads
    public fun sendStreamingMessage(name: String? = null): SendStreamingMessageBuildingStep =
        sendMessageStreaming(name)

    /**
     * Configures the behavior of the mocking server to handle
     * [Get a Task](https://a2a-protocol.org/latest/specification/) requests.
     *
     * This method defines a mock server behavior by simulating the A2A's
     * [Get a Task](https://a2a-protocol.org/latest/specification/) endpoint. It creates
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
                        it?.method == "tasks/pushNotificationConfig/get"
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
                        it?.method == "tasks/pushNotificationConfig/set"
                    }
                }

        return SetTaskPushNotificationBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun listTaskPushNotificationConfig(
        name: String? = null,
    ): ListTaskPushNotificationConfigBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = ListTaskPushNotificationConfigRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/pushNotificationConfig/list"
                    }
                }

        return ListTaskPushNotificationConfigBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    @JvmOverloads
    public fun deleteTaskPushNotificationConfig(
        name: String? = null,
    ): DeleteTaskPushNotificationConfigBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = DeleteTaskPushNotificationConfigRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "tasks/pushNotificationConfig/delete"
                    }
                }

        return DeleteTaskPushNotificationConfigBuildingStep(
            mokksy = mokksy,
            buildingStep = requestStep,
        )
    }

    /**
     * Configures the behavior of the mocking server to handle
     * [Resubscribe to a Task](https://a2a-protocol.org/latest/specification/) requests.
     *
     * This method simulates the behavior of the A2A's
     * [Resubscribe to a Task](https://a2a-protocol.org/latest/specification/) endpoint.
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

    @JvmOverloads
    public fun getAuthenticatedExtendedCard(
        name: String? = null,
    ): GetAuthenticatedExtendedCardBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = GetAuthenticatedExtendedCardRequest::class) {
                    path("/")
                    bodyMatchesPredicate {
                        it?.method == "agent/getAuthenticatedExtendedCard"
                    }
                }

        return GetAuthenticatedExtendedCardBuildingStep(
            mokksy = mokksy,
            buildingStep = requestStep,
        )
    }

    public fun getTaskNotifications(taskId: TaskId): TaskNotificationHistory =
        notificationListener.getByTaskId(taskId)

    @JvmOverloads
    public suspend fun sendPushNotification(
        config: PushNotificationConfig = PushNotificationConfig(url = notificationUrl()),
        event: TaskUpdateEvent,
    ) {
        notificationSender.sendPushNotification(config, event)
    }
}
