package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.CancelTaskRequest
import me.kpavlov.aimocks.a2a.model.CancelTaskResponse
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.a2a.model.PushNotificationConfig
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent

/**
 * Client for interacting with an A2A (Agent-to-Agent) server.
 *
 * This client provides methods for all operations supported by the A2A protocol,
 * including sending tasks, getting tasks, canceling tasks, and managing push notifications.
 * It also supports streaming operations.
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
     * @return The agent card.
     */
    public suspend fun getAgentCard(): AgentCard

    /**
     * Sends a task to the server.
     *
     * @param params The parameters for sending the task.
     * @return The response containing the task.
     */
    public suspend fun sendTask(params: TaskSendParams): SendTaskResponse

    /**
     * Gets a task from the server.
     *
     * @param id The ID of the task to get.
     * @param historyLength The number of history items to include (optional).
     * @return The response containing the task.
     */
    public suspend fun getTask(
        id: TaskId,
        historyLength: Int? = null,
    ): GetTaskResponse

    public suspend fun getTask(request: GetTaskRequest): GetTaskResponse

    /**
     * Cancels a task on the server.
     *
     * @param id The ID of the task to cancel.
     * @return The response containing the canceled task.
     */
    public suspend fun cancelTask(id: TaskId): CancelTaskResponse

    /**
     * Cancels a task on the server.
     *
     * @param request [CancelTaskRequest]
     * @return The response containing the canceled task.
     */
    public suspend fun cancelTask(request: CancelTaskRequest): CancelTaskResponse

    /**
     * Sets push notification configuration for a task.
     *
     * @param id The ID of the task.
     * @param config The push notification configuration.
     * @return The response containing the push notification configuration.
     */
    public suspend fun setTaskPushNotification(
        id: TaskId,
        config: PushNotificationConfig,
    ): SetTaskPushNotificationResponse

    /**
     * Gets the push notification configuration for a task.
     *
     * @param id The ID of the task.
     * @return The response containing the push notification configuration.
     */
    public suspend fun getTaskPushNotification(id: TaskId): GetTaskPushNotificationResponse

    /**
     * Sends a task to the server and subscribes to streaming updates.
     *
     * @param params The parameters for sending the task.
     * @return A flow of task update events.
     */
    public fun sendTaskStreaming(params: TaskSendParams): Flow<TaskUpdateEvent>

    /**
     * Resubscribes to streaming updates for a task.
     *
     * @param id The ID of the task.
     * @return A flow of task update events.
     */
    public fun resubscribeToTask(id: TaskId): Flow<TaskUpdateEvent>

    /**
     * Closes the client and releases resources.
     */
    public fun close()
}
