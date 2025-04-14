package me.kpavlov.aimocks.a2a

import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.mokksy.MokksyServer

public class CardBuildingStep(
    private val mokksy: MokksyServer,
) {
    public infix fun responds(card: AgentCard) {
        mokksy
            .get {
                this.path("/.well-known/agent.json")
            }.respondsWith<AgentCard> {
                this.body = card
            }
    }
}
