package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.AgentCard
import dev.mokksy.aimocks.a2a.model.AgentCardBuilder
import dev.mokksy.aimocks.a2a.model.GetAuthenticatedExtendedCardRequest
import dev.mokksy.aimocks.a2a.model.GetAuthenticatedExtendedCardResponse
import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.a2a.model.RequestId
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

/**
 * Response specification for configuring get authenticated extended card responses.
 */
public class GetAuthenticatedExtendedCardResponseSpecification(
    response: AbstractResponseDefinition<GetAuthenticatedExtendedCardResponse>,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<GetAuthenticatedExtendedCardRequest, GetAuthenticatedExtendedCardResponse>(
        response = response,
        delay = delay,
    ) {
    public var id: RequestId? = null
    public var result: AgentCard? = null
    public var error: JSONRPCError? = null

    /**
     * Sets the request ID.
     */
    public fun id(id: RequestId): GetAuthenticatedExtendedCardResponseSpecification =
        apply { this.id = id }

    /**
     * Sets the result using a lambda with receiver.
     */
    public fun result(
        init: AgentCardBuilder.() -> Unit,
    ): GetAuthenticatedExtendedCardResponseSpecification =
        apply {
            this.result = AgentCardBuilder().apply(init).build()
        }

    /**
     * Builds the response.
     */
    public fun build(): GetAuthenticatedExtendedCardResponse =
        GetAuthenticatedExtendedCardResponse(
            id = id,
            result = result,
            error = error,
        )
}
