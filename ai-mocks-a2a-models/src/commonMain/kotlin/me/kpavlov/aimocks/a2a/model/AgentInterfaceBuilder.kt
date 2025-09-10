package me.kpavlov.aimocks.a2a.model

/**
 * Builder for creating [AgentInterface] instances.
 *
 * Use this DSL to declare additional interfaces (transport + URL) for an agent.
 */
public class AgentInterfaceBuilder {
    public var transport: Transport? = null
    public var url: String? = null

    /** Sets the [Transport] protocol for the interface. */
    public fun transport(transport: Transport): AgentInterfaceBuilder =
        apply { this.transport = transport }

    /** Sets the endpoint URL where this interface is available. */
    public fun url(url: String): AgentInterfaceBuilder = apply { this.url = url }

    /** Builds the [AgentInterface], validating required fields. */
    public fun build(): AgentInterface {
        val t = requireNotNull(transport) { "transport must be set" }
        val u = requireNotNull(url) { "url must be set" }
        return AgentInterface(
            transport = t,
            url = u,
        )
    }
}

/** Top-level DSL to create an [AgentInterface]. */
public fun agentInterface(builder: AgentInterfaceBuilder.() -> Unit): AgentInterface =
    AgentInterfaceBuilder().apply(builder).build()



