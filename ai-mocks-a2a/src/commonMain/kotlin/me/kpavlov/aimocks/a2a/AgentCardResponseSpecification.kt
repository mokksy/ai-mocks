package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.AgentCardBuilder
import me.kpavlov.aimocks.core.ResponseSpecification
import me.kpavlov.mokksy.response.AbstractResponseDefinition
import kotlin.time.Duration

public class AgentCardResponseSpecification(
    response: AbstractResponseDefinition<AgentCard>,
    public var card: AgentCard? = null,
    public var delay: Duration = Duration.ZERO,
) : ResponseSpecification<Nothing, AgentCard>(response = response) {
    public fun card(block: AgentCardBuilder.() -> Unit) {
        this.card = AgentCardBuilder().apply(block).build()
    }
}
