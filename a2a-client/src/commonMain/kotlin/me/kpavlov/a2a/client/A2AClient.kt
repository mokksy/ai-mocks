package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.CancelTaskRequest
import me.kpavlov.aimocks.a2a.model.CancelTaskResponse
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.a2a.model.MessageSendParams
import me.kpavlov.aimocks.a2a.model.PushNotificationConfig
import me.kpavlov.aimocks.a2a.model.SendMessageRequest
import me.kpavlov.aimocks.a2a.model.SendMessageResponse
import me.kpavlov.aimocks.a2a.model.SendStreamingMessageRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent

/**
 * Client for interacting with an A2A (Agent-to-Agent) server.
 *
 * This client provides methods for all operations supported by the A2A protocol,
 * including sending tasks, getting tasks, canceling tasks, and managing push notifications.
 * It also supports streaming operations for real-time task updates.
 *
 * The A2A protocol enables communication between AI agents, allowing them to work
 * together on complex tasks. This client implementation handles the HTTP communication
 * details and data serialization/deserialization required by the protocol.
 */
@Suppress("TooManyFunctions")
public interface A2AClient {
    /**
     * Gets the underlying HTTP client.
     */
    public val httpClient: HttpClient

    /**
     * Gets the agent card from the server.
     *
     * Agent cards provide metadata about an agent, including its capabilities,
     * identity, and other relevant information, according to the A2A protocol.
     * This metadata helps other agents understand how to interact with this agent.
     *
     * @return The agent card containing agent metadata.
     */
    public suspend fun getAgentCard(): AgentCard

    /**
     * Sends a message to an agent using the A2A protocol's message/send method.
     *
     * This is the primary method for agent-to-agent communication in the A2A protocol.
     * It allows sending messages containing text, files, or structured data to another agent.
     *
     * @param params The parameters for sending the message, including the message content.
     * @return The response containing the created task with assigned ID and initial status.
     * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
     */
    public suspend fun sendMessage(params: MessageSendParams): SendMessageResponse

    /**
     * Sends a message to an agent using a pre-constructed request object.
     *
     * This method allows for more control over the request, including setting a custom
     * request ID and complete request parameters.
     *
     * @param request The fully constructed message request containing ID and parameters.
     * @return The response containing the created task with assigned ID and initial status.
     * @see [A2A Protocol - Send a Message](https://a2a-protocol.org/latest/specification/)
     */
    public suspend fun sendMessage(request: SendMessageRequest): SendMessageResponse

    /**
     * Gets a task from the server.
     *
     * Retrieves a task by its ID, optionally including a specified number of
     * history items to show the task's progression over time.
     *
     * @param id The ID of the task to get.
     * @param historyLength The number of history items to include (optional).
     * @return The response containing the task details, status, and history if requested.
     */
    public suspend fun getTask(
        id: TaskId,
        historyLength: Int? = null,
    ): GetTaskResponse

    /**
     * Gets a task from the server using a pre-constructed request object.
     *
     * This method allows for more control over the request, including setting a custom
     * request ID and complete query parameters.
     *
     * @param request The fully constructed task request containing ID and query parameters.
     * @return The response containing the task details, status, and history if requested.
     */
    public suspend fun getTask(request: GetTaskRequest): GetTaskResponse

    /**
     * Cancels a task on the server.
     *
     * Sends a request to stop processing a task. Depending on the server implementation,
     * a canceled task might be immediately terminated or gracefully completed.
     *
     * @param id The ID of the task to cancel.
     * @return The response containing the canceled task with the updated status.
     */
    public suspend fun cancelTask(id: TaskId): CancelTaskResponse

    /**
     * Cancels a task on the server using a pre-constructed request object.
     *
     * This method allows for more control over the request, including setting a custom
     * request ID and complete cancellation parameters.
     *
     * @param request The fully constructed cancellation request containing request ID and task parameters.
     * @return The response containing the canceled task with the updated status.
     */
    public suspend fun cancelTask(request: CancelTaskRequest): CancelTaskResponse

    /**
     * Sets push notification configuration for a task.
     *
     * Configures how notifications about task status changes should be delivered.
     * This allows clients to receive updates about task progress without polling.
     *
     * @param id The ID of the task.
     * @param config The push notification configuration containing delivery settings.
     * @return The response containing the updated push notification configuration.
     */
    public suspend fun setTaskPushNotification(
        id: TaskId,
        config: PushNotificationConfig,
    ): SetTaskPushNotificationResponse

    /**
     * Sets push notification configuration for a task using a pre-constructed request object.
     *
     * This method allows for more control over the request, including setting a custom
     * request ID and complete notification parameters.
     *
     * @param request The fully constructed request containing ID, task ID, and notification configuration.
     * @return The response containing the updated push notification configuration.
     */
    public suspend fun setTaskPushNotification(
        request: SetTaskPushNotificationRequest,
    ): SetTaskPushNotificationResponse

    /**
     * Gets the push notification configuration for a task.
     *
     * Retrieves the current settings for how notifications about task status changes
     * are delivered for the specified task.
     *
     * @param id The ID of the task.
     * @return The response containing the current push notification configuration.
     */
    public suspend fun getTaskPushNotification(id: TaskId): GetTaskPushNotificationResponse

    /**
     * Gets the push notification configuration for a task using a pre-constructed request object.
     *
     * This overload allows for more control over the request, including setting a custom
     * request ID and complete query parameters.
     *
     * @param request The fully constructed request containing request ID and task parameters.
     * @return The response containing the current push notification configuration.
     */
    public suspend fun getTaskPushNotification(
        request: GetTaskPushNotificationRequest,
    ): GetTaskPushNotificationResponse

    /**
     * Sends a message to an agent with streaming updates using the A2A protocol's message/stream method.
     *
     * This method creates a new message and establishes a streaming connection that provides
     * real-time updates about the task's progress via Server-Sent Events (SSE).
     *
     * @param params The parameters for sending the message, including input data and requirements.
     * @return A flow of task update events that emits as the task progresses until completion.
     * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
     */
    public fun sendStreamingMessage(params: MessageSendParams): Flow<TaskUpdateEvent>

    /**
     * Sends a message to an agent with streaming updates using a pre-constructed request object.
     *
     * This method allows for more control over the request, including setting a custom
     * request ID and complete streaming parameters.
     *
     * @param request The fully constructed streaming message request containing ID and parameters.
     * @return A flow of task update events that emits as the task progresses until completion.
     * @see [A2A Protocol - Streaming Messages](https://a2a-protocol.org/latest/specification/)
     */
    public fun sendStreamingMessage(request: SendStreamingMessageRequest): Flow<TaskUpdateEvent>

    /**
     * Resubscribes to streaming updates for a task.
     *
     * This method reconnects to an existing task's event stream, useful when a previous
     * streaming connection was lost or closed. It allows clients to resume receiving updates
     * about task progress without missing events that occurred during the disconnection period.
     *
     * @param id The ID of the task to reconnect to.
     * @return A flow of task update events that continues from the current task state until completion.
     */
    public fun resubscribeToTask(id: TaskId): Flow<TaskUpdateEvent>

    /**
     * Closes the client and releases resources.
     *
     * This method should be called when the client is no longer needed to ensure
     * proper cleanup of network connections and other resources. After closing,
     * the client instance should not be used for any further operations.
     */
    public fun close()
}
