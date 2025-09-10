package me.kpavlov.aimocks.a2a.model

/**
 * Builder for creating [AgentExtension] instances.
 */
public class AgentExtensionBuilder {
    public var description: String? = null
    public var params: Data? = null
    public var required: Boolean? = null
    public var uri: String? = null

    public fun description(description: String): AgentExtensionBuilder =
        apply { this.description = description }

    public fun params(params: Data): AgentExtensionBuilder = apply { this.params = params }

    public fun params(params: Map<String, Any>): AgentExtensionBuilder =
        apply { this.params = Data.of(params) }

    public fun required(required: Boolean): AgentExtensionBuilder =
        apply { this.required = required }

    public fun uri(uri: String): AgentExtensionBuilder = apply { this.uri = uri }

    public fun build(): AgentExtension {
        val u = requireNotNull(uri) { "uri must be set" }
        return AgentExtension(
            description = description,
            params = params,
            required = required,
            uri = u,
        )
    }
}

public fun agentExtension(builder: AgentExtensionBuilder.() -> Unit): AgentExtension =
    AgentExtensionBuilder().apply(builder).build()
