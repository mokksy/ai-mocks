package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.json.JsonElement
import java.util.function.Consumer

/**
 * DSL builder for [DeleteTaskPushNotificationConfigParams].
 *
 * Example usage:
 * ```
 * val params = deleteTaskPushNotificationConfigParams {
 *     id = "task_12345"
 * }
 * ```
 */
public class DeleteTaskPushNotificationConfigParamsBuilder {
    public var id: TaskId? = null
    public var metadata: Map<String, JsonElement>? = null

    /**
     * Sets the ID of the task.
     *
     * @param id The ID of the task.
     * @return This builder instance for method chaining.
     */
    public fun id(id: TaskId): DeleteTaskPushNotificationConfigParamsBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the metadata for the request.
     *
     * @param metadata The metadata map.
     * @return This builder instance for method chaining.
     */
    public fun metadata(
        metadata: Map<String, JsonElement>,
    ): DeleteTaskPushNotificationConfigParamsBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds a [DeleteTaskPushNotificationConfigParams] instance with the configured parameters.
     *
     * @return A new [DeleteTaskPushNotificationConfigParams] instance.
     * @throws IllegalArgumentException if required fields are missing.
     */
    public fun build(): DeleteTaskPushNotificationConfigParams {
          val id = requireNotNull(this.id) { "id is required" }
          return DeleteTaskPushNotificationConfigParams(
                  id = id,
                  metadata = metadata,
              )
         }
}

/**
 * Top-level DSL function for creating [DeleteTaskPushNotificationConfigParams].
 *
 * @param init The lambda to configure the delete task push notification config params.
 * @return A new [DeleteTaskPushNotificationConfigParams] instance.
 */
public inline fun deleteTaskPushNotificationConfigParams(
    init: DeleteTaskPushNotificationConfigParamsBuilder.() -> Unit,
): DeleteTaskPushNotificationConfigParams =
    DeleteTaskPushNotificationConfigParamsBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [DeleteTaskPushNotificationConfigParams].
 *
 * @param init The consumer to configure the delete task push notification config params.
 * @return A new [DeleteTaskPushNotificationConfigParams] instance.
 */
public fun deleteTaskPushNotificationConfigParams(
    init: Consumer<DeleteTaskPushNotificationConfigParamsBuilder>,
): DeleteTaskPushNotificationConfigParams {
    val builder = DeleteTaskPushNotificationConfigParamsBuilder()
    init.accept(builder)
    return builder.build()
}
