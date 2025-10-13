package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigRequest
import dev.mokksy.aimocks.a2a.model.DeleteTaskPushNotificationConfigResponse
import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.JSONRPCErrorBuilder
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Response specification for configuring delete task push notification config responses.
 */
public class DeleteTaskPushNotificationConfigResponseSpecification(
    response: AbstractResponseDefinition<DeleteTaskPushNotificationConfigResponse>,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<DeleteTaskPushNotificationConfigRequest, DeleteTaskPushNotificationConfigResponse>(
        response = response,
        delay = delay,
    ) {
    public var id: RequestId? = null
    public var result: Nothing? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the request ID.
     */
    public fun id(id: RequestId): DeleteTaskPushNotificationConfigResponseSpecification =
        apply { this.id = id }

    /**
     * Sets the error using a lambda with receiver.
     */
    public fun error(
        init: JSONRPCErrorBuilder<JSONRPCError, JSONRPCErrorBuilder<JSONRPCError, *>>.() -> Unit,
    ): DeleteTaskPushNotificationConfigResponseSpecification =
        apply {
            this.error =
                JSONRPCErrorBuilder<JSONRPCError, JSONRPCErrorBuilder<JSONRPCError, *>>()
                    .apply(
                        init,
                    ).build()
        }

    /**
     * Builds the response.
     */
    public fun build(): DeleteTaskPushNotificationConfigResponse =
        DeleteTaskPushNotificationConfigResponse(
            id = id,
            result = result,
            error = error,
        )
}
