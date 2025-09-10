package me.kpavlov.aimocks.a2a.model

/**
 * Builder for [GetTaskPushNotificationConfigParams].
 */
public class GetTaskPushNotificationConfigParamsBuilder {
    private var id: String? = null
    private var pushNotificationConfigId: String? = null
    private var metadata: Metadata? = null

    /**
     * Sets the unique identifier of the task.
     */
    public fun id(id: String): GetTaskPushNotificationConfigParamsBuilder = apply { this.id = id }

    /**
     * Sets the ID of the push notification configuration to retrieve.
     */
    public fun pushNotificationConfigId(pushNotificationConfigId: String?): GetTaskPushNotificationConfigParamsBuilder =
        apply {
            this.pushNotificationConfigId = pushNotificationConfigId
        }

    /**
     * Sets optional metadata associated with the request.
     */
    public fun metadata(metadata: Metadata?): GetTaskPushNotificationConfigParamsBuilder =
        apply { this.metadata = metadata }

    /**
     * Builds the [GetTaskPushNotificationConfigParams].
     */
    public fun build(): GetTaskPushNotificationConfigParams {
        val id = requireNotNull(this.id) { "id is required" }
        return GetTaskPushNotificationConfigParams(
            id = id,
            pushNotificationConfigId = pushNotificationConfigId,
            metadata = metadata,
        )
    }
}

/**
 * Creates a new [GetTaskPushNotificationConfigParams] using the builder pattern.
 */
public inline fun GetTaskPushNotificationConfigParams(
    block: GetTaskPushNotificationConfigParamsBuilder.() -> Unit,
): GetTaskPushNotificationConfigParams =
    GetTaskPushNotificationConfigParamsBuilder().apply(block).build()
