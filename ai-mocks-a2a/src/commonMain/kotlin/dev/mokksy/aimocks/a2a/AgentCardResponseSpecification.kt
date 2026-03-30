package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.AgentCard
import dev.mokksy.aimocks.a2a.model.AgentCardBuilder
import dev.mokksy.aimocks.a2a.model.JSONRPCError
import dev.mokksy.aimocks.core.AbstractResponseSpecification
import kotlin.time.Duration

public class AgentCardResponseSpecification(
    public var card: AgentCard? = null,
    public var error: JSONRPCError? = null,
    delay: Duration = Duration.ZERO,
) : AbstractResponseSpecification<Nothing, AgentCard>(delay = delay) {
    public fun card(block: AgentCardBuilder.() -> Unit) {
        this.card = AgentCardBuilder().apply(block).build()
    }
}
