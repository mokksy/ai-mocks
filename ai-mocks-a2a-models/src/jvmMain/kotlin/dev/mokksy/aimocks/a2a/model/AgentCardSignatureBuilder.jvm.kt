package dev.mokksy.aimocks.a2a.model

import java.util.function.Consumer

public fun agentCardSignature(init: Consumer<AgentCardSignatureBuilder>): AgentCardSignature {
    val builder = AgentCardSignatureBuilder()
    init.accept(builder)
    return builder.build()
}

public fun AgentCardSignature.Companion.create(block: Consumer<AgentCardSignatureBuilder>): AgentCardSignature =
    agentCardSignature(block)
