package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Java-friendly top-level DSL function for creating [AgentProvider].
 *
 * @param init The consumer to configure the agent provider.
 * @return A new [AgentProvider] instance.
 */
public fun agentProvider(init: Consumer<AgentProviderBuilder>): AgentProvider {
    val builder = AgentProviderBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of an AgentProvider using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building an AgentProvider instance using the AgentProviderBuilder.
 * @return A newly created [AgentProvider] instance.
 */
public fun AgentProvider.Companion.create(block: Consumer<AgentProviderBuilder>): AgentProvider =
    agentProvider(block)
