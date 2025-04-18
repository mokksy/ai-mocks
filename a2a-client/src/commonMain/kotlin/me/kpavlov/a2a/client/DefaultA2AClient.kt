package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.CancelTaskRequest
import me.kpavlov.aimocks.a2a.model.CancelTaskResponse
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.GetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.a2a.model.GetTaskResponse
import me.kpavlov.aimocks.a2a.model.PushNotificationConfig
import me.kpavlov.aimocks.a2a.model.SendTaskRequest
import me.kpavlov.aimocks.a2a.model.SendTaskResponse
import me.kpavlov.aimocks.a2a.model.SendTaskStreamingRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationRequest
import me.kpavlov.aimocks.a2a.model.SetTaskPushNotificationResponse
import me.kpavlov.aimocks.a2a.model.TaskId
import me.kpavlov.aimocks.a2a.model.TaskIdParams
import me.kpavlov.aimocks.a2a.model.TaskPushNotificationConfig
import me.kpavlov.aimocks.a2a.model.TaskQueryParams
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import java.util.UUID

/**
 * Default implementation of the A2AClient interface.
 *
 * @property httpClient The Ktor HTTP client used for making requests.
 * @property baseUrl The base URL of the A2A server.
 * @property json The JSON serializer/deserializer.
 */
@Suppress("TooManyFunctions")
public class DefaultA2AClient(
    override val httpClient: HttpClient,
    private val baseUrl: String,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) : A2AClient {

    /**
     * Gets the agent card from the server.
     *
     * @return The agent card.
     */
    override suspend fun getAgentCard(): AgentCard {
        val response = httpClient.get("$baseUrl/.well-known/agent.json")
        return response.body<AgentCard>()
    }

    /**
     * Sends a task to the server.
     *
     * @param params The parameters for sending the task.
     * @return The response containing the task.
     */
    override suspend fun sendTask(params: TaskSendParams): SendTaskResponse {
        val request = SendTaskRequest.create {
            id = "1"
            this.params = params
        }

        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(SendTaskRequest.serializer(), request))
        }

        return response.body<String>().let { json.decodeFromString<SendTaskResponse>(it) }
    }

    /**
     * Gets a task from the server.
     *
     * @param id The ID of the task to get.
     * @param historyLength The number of history items to include (optional).
     * @return The response containing the task.
     */
    override suspend fun getTask(id: TaskId, historyLength: Int?): GetTaskResponse {
        val request = GetTaskRequest(
            id = "1",
            params = TaskQueryParams(
                id = id,
                historyLength = historyLength?.toLong()
            )
        )
        return getTask(request)
    }

    override suspend fun getTask(request: GetTaskRequest): GetTaskResponse {
        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return response.body<GetTaskResponse>()
    }

    /**
     * Cancels a task on the server.
     *
     * @param id The ID of the task to cancel.
     * @return The response containing the canceled task.
     */
    override suspend fun cancelTask(id: TaskId): CancelTaskResponse {
        val request = CancelTaskRequest(
            id = UUID.randomUUID().toString(),
            params = TaskIdParams(
                id = id
            )
        )
        return cancelTask(request)
    }


    override suspend fun cancelTask(request: CancelTaskRequest): CancelTaskResponse {
        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        return response.body<CancelTaskResponse>()
    }

    /**
     * Sets push notification configuration for a task.
     *
     * @param id The ID of the task.
     * @param config The push notification configuration.
     * @return The response containing the push notification configuration.
     */
    override suspend fun setTaskPushNotification(
        id: TaskId,
        config: PushNotificationConfig
    ): SetTaskPushNotificationResponse {
        val request = SetTaskPushNotificationRequest(
            id = "1",
            params = TaskPushNotificationConfig(
                id = id,
                pushNotificationConfig = config
            )
        )

        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(SetTaskPushNotificationRequest.serializer(), request))
        }

        return response.body<String>().let { json.decodeFromString<SetTaskPushNotificationResponse>(it) }
    }

    /**
     * Gets the push notification configuration for a task.
     *
     * @param id The ID of the task.
     * @return The response containing the push notification configuration.
     */
    override suspend fun getTaskPushNotification(id: TaskId): GetTaskPushNotificationResponse {
        val request = GetTaskPushNotificationRequest(
            id = "1",
            params = TaskIdParams(
                id = id
            )
        )

        val response = httpClient.post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(GetTaskPushNotificationRequest.serializer(), request))
        }

        return response.body<String>().let { json.decodeFromString<GetTaskPushNotificationResponse>(it) }
    }

    /**
     * Sends a task to the server and subscribes to streaming updates.
     *
     * @param params The parameters for sending the task.
     * @return A flow of task update events.
     */
    @OptIn(InternalAPI::class)
    override fun sendTaskStreaming(params: TaskSendParams): Flow<TaskUpdateEvent> {
        val payload = SendTaskStreamingRequest(
            id = "1",
            params = params
        )

        return flow {
            httpClient.sse(
                request = {
                    url { baseUrl }
                    method = HttpMethod.Post
                    body = TextContent(
                        text = json.encodeToString(SendTaskStreamingRequest.serializer(), payload),
                        contentType = ContentType.Application.Json
                    )
                }
            ) {
                var reading = true
                while (reading) {
                    incoming.collect { event ->
                        event.data?.let { data ->
                            val taskEvent = json.decodeFromString<TaskUpdateEvent>(data)
                            emit(taskEvent)

                            // Check if this is the final event
                            if (taskEvent is me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent && taskEvent.final) {
                                reading = false
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Resubscribes to streaming updates for a task.
     *
     * @param id The ID of the task.
     * @return A flow of task update events.
     */
    @OptIn(InternalAPI::class)
    override fun resubscribeToTask(id: TaskId): Flow<TaskUpdateEvent> {
        val payload = """{"jsonrpc":"2.0","id":1,"method":"tasks/resubscribe","params":{"id":"$id"}}"""

        return flow {
            httpClient.sse(
                request = {
                    url { baseUrl }
                    method = HttpMethod.Post
                    body = TextContent(
                        text = payload,
                        contentType = ContentType.Application.Json
                    )
                }
            ) {
                var reading = true
                while (reading) {
                    incoming.collect { event ->
                        event.data?.let { data ->
                            val taskEvent = json.decodeFromString<TaskUpdateEvent>(data)
                            emit(taskEvent)

                            // Check if this is the final event
                            if (taskEvent is TaskStatusUpdateEvent && taskEvent.final) {
                                reading = false
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Closes the client and releases resources.
     */
    override fun close() {
        httpClient.close()
    }
}
