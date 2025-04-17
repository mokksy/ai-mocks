package me.kpavlov.aimocks.a2a.model

public class AgentCardBuilder {
    public var name: String? = null
    public var description: String? = null
    public var url: String? = null
    public var provider: AgentProvider? = null
    public var version: String? = null
    public var documentationUrl: String? = null
    public var capabilities: AgentCapabilities? = null
    public var authentication: AgentAuthentication? = null
    public var defaultInputModes: List<String> = listOf("text")
    public var defaultOutputModes: List<String> = listOf("text")
    public var skills: List<AgentSkill>? = null

    public fun build(validate: Boolean = false): AgentCard {
        if (validate) {
            requireNotNull(name) { "name must not be null" }
            requireNotNull(url) { "url must not be null" }
            requireNotNull(version) { "version must not be null" }
            requireNotNull(capabilities) { "capabilities must not be null" }
            requireNotNull(skills) { "skills must not be null" }
        }
        return AgentCard(
            name = name!!,
            description = description,
            url = url!!,
            provider = provider,
            version = version!!,
            documentationUrl = documentationUrl,
            capabilities = capabilities!!,
            authentication = authentication,
            defaultInputModes = defaultInputModes,
            defaultOutputModes = defaultOutputModes,
            skills = skills!!,
        )
    }

    public fun provider(block: AgentProviderBuilder.() -> Unit) {
        provider = AgentProviderBuilder().apply(block).build()
    }

    public fun authentication(block: AgentAuthenticationBuilder.() -> Unit) {
        authentication = AgentAuthenticationBuilder().apply(block).build()
    }

    public fun capabilities(block: AgentCapabilitiesBuilder.() -> Unit) {
        capabilities = AgentCapabilitiesBuilder().apply(block).build()
    }
}

// Extension function for convenient creation
public fun AgentCard.Companion.create(init: AgentCardBuilder.() -> Unit): AgentCard =
    AgentCardBuilder().apply(init).build()
