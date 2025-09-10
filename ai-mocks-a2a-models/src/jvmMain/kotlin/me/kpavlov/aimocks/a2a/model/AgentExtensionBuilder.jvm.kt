package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Java-friendly top-level DSL function for creating [AgentExtension].
 */
public fun agentExtension(init: Consumer<AgentExtensionBuilder>): AgentExtension {
    val builder = AgentExtensionBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Java-friendly companion create for [AgentExtension].
 */
public fun AgentExtension.Companion.create(block: Consumer<AgentExtensionBuilder>): AgentExtension =
    agentExtension(block)
