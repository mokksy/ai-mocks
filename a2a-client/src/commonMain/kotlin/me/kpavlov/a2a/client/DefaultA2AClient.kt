package me.kpavlov.a2a.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.A2ARequest
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
import me.kpavlov.aimocks.a2a.model.TaskResubscriptionRequest
import me.kpavlov.aimocks.a2a.model.TaskSendParams
import me.kpavlov.aimocks.a2a.model.TaskStatusUpdateEvent
import me.kpavlov.aimocks.a2a.model.TaskUpdateEvent
import me.kpavlov.aimocks.a2a.model.create
import java.util.UUID

/**
 * Default implementation of the A2AClient interface.
 *
 * @property httpClient The Ktor HTTP client used for making requests.
 * @property baseUrl The base URL of the A2A server.
 * @property json The JSON serializer/deserializer.
 */
@Suppress("TooManyFunctions", "unused")
public class DefaultA2AClient(
    override val httpClient: HttpClient,
    private val baseUrl: String,
    private val json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        },
    private val requestConfigurer: HttpRequestBuilder.() -> Unit = { },
) : A2AClient {
    override suspend fun getAgentCard(): AgentCard {
        val response =
            httpClient.get("$baseUrl/.well-known/agent.json") {
                requestConfigurer.invoke(this)
            }
        return response.body<AgentCard>()
    }

    override suspend fun sendTask(params: TaskSendParams): SendTaskResponse =
        sendTask(
            request =
                SendTaskRequest.create {
                    this.id = "1"
                    this.params = params
                },
        )

    public override suspend fun sendTask(request: SendTaskRequest): SendTaskResponse {
        val response =
            httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(SendTaskRequest.serializer(), request))
                requestConfigurer.invoke(this)
            }

        return response.body<SendTaskResponse>()
    }

    override suspend fun getTask(
        id: TaskId,
        historyLength: Int?,
    ): GetTaskResponse {
        val request =
            GetTaskRequest(
                id = "1",
                params =
                    TaskQueryParams(
                        id = id,
                        historyLength = historyLength?.toLong(),
                    ),
            )
        return getTask(request)
    }

    override suspend fun getTask(request: GetTaskRequest): GetTaskResponse {
        val response =
            httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(request)
                requestConfigurer.invoke(this)
            }

        return response.body<GetTaskResponse>()
    }

    override suspend fun cancelTask(id: TaskId): CancelTaskResponse {
        val request =
            CancelTaskRequest(
                id = UUID.randomUUID().toString(),
                params =
                    TaskIdParams(
                        id = id,
                    ),
            )
        return cancelTask(request)
    }

    override suspend fun cancelTask(request: CancelTaskRequest): CancelTaskResponse {
        val response =
            httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(request)
                requestConfigurer.invoke(this)
            }

        return response.body<CancelTaskResponse>()
    }

    override suspend fun setTaskPushNotification(
        id: TaskId,
        config: PushNotificationConfig,
    ): SetTaskPushNotificationResponse {
        val request =
            SetTaskPushNotificationRequest(
                id = "1",
                params =
                    TaskPushNotificationConfig(
                        id = id,
                        pushNotificationConfig = config,
                    ),
            )

        return setTaskPushNotification(request)
    }

    public override suspend fun setTaskPushNotification(
        request: SetTaskPushNotificationRequest,
    ): SetTaskPushNotificationResponse {
        val response =
            httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(request)
                requestConfigurer.invoke(this)
            }

        return response.body<SetTaskPushNotificationResponse>()
    }

    override suspend fun getTaskPushNotification(id: TaskId): GetTaskPushNotificationResponse {
        val request =
            GetTaskPushNotificationRequest(
                id = "1",
                params =
                    TaskIdParams(
                        id = id,
                    ),
            )

        return getTaskPushNotification(request)
    }

    public override suspend fun getTaskPushNotification(
        request: GetTaskPushNotificationRequest,
    ): GetTaskPushNotificationResponse {
        val response =
            httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(request)
                requestConfigurer.invoke(this)
            }

        return response.body<GetTaskPushNotificationResponse>()
    }

    override fun sendTaskStreaming(params: TaskSendParams): Flow<TaskUpdateEvent> {
        val payload =
            SendTaskStreamingRequest(
                id = "1",
                params = params,
            )

        return sendTaskStreaming(payload)
    }

    public override fun sendTaskStreaming(
        request: SendTaskStreamingRequest,
    ): Flow<TaskUpdateEvent> = receiveTaskUpdateEvents(request, requestConfigurer)

    override fun resubscribeToTask(id: TaskId): Flow<TaskUpdateEvent> {
        val request =
            TaskResubscriptionRequest(
                id = "1",
                params = TaskQueryParams(id = id),
            )
        return receiveTaskUpdateEvents(request, requestConfigurer)
    }

    override fun close() {
        httpClient.close()
    }

    private inline fun <reified T : A2ARequest> receiveTaskUpdateEvents(
        request: T,
        crossinline configurer: HttpRequestBuilder.() -> Unit,
    ): Flow<TaskUpdateEvent> =
        flow {
            httpClient.sse(
                request = {
                    url { baseUrl }
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody(request)
                    configurer.invoke(this)
                },
            ) {
                var reading = true
                while (reading) {
                    incoming.collect { event ->
                        event.data?.let { data ->
                            val taskEvent = json.decodeFromString<TaskUpdateEvent>(data)
                            emit(taskEvent)

                            // Check if this is the final event
                            if (taskEvent is TaskStatusUpdateEvent &&
                                taskEvent.final
                            ) {
                                reading = false
                            }
                        }
                    }
                }
            }
        }
}
