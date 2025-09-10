package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [AgentCard] instances.
 *
 * This builder provides a fluent API for creating AgentCard objects,
 * making it easier to configure agent cards.
 */
@Suppress("TooManyFunctions")
public class AgentCardBuilder {
    public var name: String? = null
    public var description: String? = null
    public var url: String? = null
    public var preferredTransport: Transport = Transport.JSONRPC
    public var additionalInterfaces: MutableList<AgentInterface>? = null
    public var iconUrl: String? = null
    public var provider: AgentProvider? = null
    public var version: String? = null
    public var documentationUrl: String? = null
    public var capabilities: AgentCapabilities? = null
    public var defaultInputModes: List<String> = listOf("text")
    public var defaultOutputModes: List<String> = listOf("text")
    public var skills: MutableList<AgentSkill> = mutableListOf()
    public var security: List<Map<String, List<String>>>? = null
    public var securitySchemes: Map<String, SecurityScheme>? = null
    public var signatures: MutableList<AgentCardSignature>? = null
    public var supportsAuthenticatedExtendedCard: Boolean = false

    /**
     * Sets the name of the agent.
     *
     * @param name The name of the agent.
     * @return This builder instance for method chaining.
     */
    public fun name(name: String): AgentCardBuilder =
        apply {
            this.name = name
        }

    /**
     * Sets the description of the agent.
     *
     * @param description The description of the agent.
     * @return This builder instance for method chaining.
     */
    public fun description(description: String): AgentCardBuilder =
        apply {
            this.description = description
        }

    /**
     * Sets the URL of the agent.
     *
     * @param url The URL of the agent.
     * @return This builder instance for method chaining.
     */
    public fun url(url: String): AgentCardBuilder =
        apply {
            this.url = url
        }

    /**
     * Sets the provider of the agent.
     *
     * @param provider The provider of the agent.
     * @return This builder instance for method chaining.
     */
    public fun provider(provider: AgentProvider): AgentCardBuilder =
        apply {
            this.provider = provider
        }

    /**
     * Sets the version of the agent.
     *
     * @param version The version of the agent.
     * @return This builder instance for method chaining.
     */
    public fun version(version: String): AgentCardBuilder =
        apply {
            this.version = version
        }

    /**
     * Sets the documentation URL of the agent.
     *
     * @param documentationUrl The documentation URL of the agent.
     * @return This builder instance for method chaining.
     */
    public fun documentationUrl(documentationUrl: String): AgentCardBuilder =
        apply {
            this.documentationUrl = documentationUrl
        }

    /**
     * Sets the capabilities of the agent.
     *
     * @param capabilities The capabilities of the agent.
     * @return This builder instance for method chaining.
     */
    public fun capabilities(capabilities: AgentCapabilities): AgentCardBuilder =
        apply {
            this.capabilities = capabilities
        }

    /**
     * Sets the default input modes of the agent.
     *
     * @param defaultInputModes The default input modes of the agent.
     * @return This builder instance for method chaining.
     */
    public fun defaultInputModes(defaultInputModes: List<String>): AgentCardBuilder =
        apply {
            this.defaultInputModes = defaultInputModes
        }

    /**
     * Sets the default output modes of the agent.
     *
     * @param defaultOutputModes The default output modes of the agent.
     * @return This builder instance for method chaining.
     */
    public fun defaultOutputModes(defaultOutputModes: List<String>): AgentCardBuilder =
        apply {
            this.defaultOutputModes = defaultOutputModes
        }

    /**
     * Adds a skill to the agent.
     *
     * @param skill The skill to add.
     * @return This builder instance for method chaining.
     */
    public fun addSkill(skill: AgentSkill): AgentCardBuilder =
        apply {
            this.skills.add(skill)
        }

    /**
     * Sets the signatures for the agent card.
     *
     * @param signatures The list of signatures.
     * @return This builder instance for method chaining.
     */
    public fun signatures(signatures: List<AgentCardSignature>): AgentCardBuilder =
        apply {
            this.signatures = signatures.toMutableList()
        }

    /**
     * Adds a signature to the agent card.
     *
     * @param signature The signature to add.
     * @return This builder instance for method chaining.
     */
    public fun addSignature(signature: AgentCardSignature): AgentCardBuilder =
        apply {
            if (this.signatures == null) {
                this.signatures = mutableListOf()
            }
            this.signatures!!.add(signature)
        }

    /**
     * Sets whether the agent supports authenticated extended card.
     *
     * @param supportsAuthenticatedExtendedCard Whether authenticated extended card is supported.
     * @return This builder instance for method chaining.
     */
    public fun supportsAuthenticatedExtendedCard(
        supportsAuthenticatedExtendedCard: Boolean,
    ): AgentCardBuilder =
        apply {
            this.supportsAuthenticatedExtendedCard = supportsAuthenticatedExtendedCard
        }

    /**
     * Builds an [AgentCard] instance with the configured parameters.
     *
     * @param validate Whether to validate the required parameters.
     * @return A new [AgentCard] instance.
     * @throws IllegalArgumentException If validate is true and required parameters are missing.
     */
    public fun build(validate: Boolean = false): AgentCard {
        if (validate) {
            requireNotNull(name) { "name must not be null" }
            requireNotNull(description) { "description must not be null" }
            requireNotNull(url) { "url must not be null" }
            requireNotNull(version) { "version must not be null" }
            requireNotNull(capabilities) { "capabilities must not be null" }
        }
        return AgentCard(
            name = name!!,
            description = description,
            url = url!!,
            preferredTransport = preferredTransport,
            additionalInterfaces = additionalInterfaces,
            iconUrl = iconUrl,
            provider = provider,
            version = version!!,
            documentationUrl = documentationUrl,
            capabilities = capabilities!!,
            defaultInputModes = defaultInputModes,
            defaultOutputModes = defaultOutputModes,
            skills = skills,
            security = security,
            securitySchemes = securitySchemes,
            signatures = signatures,
            supportsAuthenticatedExtendedCard = supportsAuthenticatedExtendedCard,
        )
    }

    /**
     * Creates a skill using the provided configuration block.
     *
     * @param block The lambda to configure the skill.
     * @return The created skill.
     */
    public fun skill(block: AgentSkillBuilder.() -> Unit): AgentSkill =
        AgentSkillBuilder().apply(block).build()

    /**
     * Creates a skill using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the skill.
     * @return The created skill.
     */
    public fun skill(block: Consumer<AgentSkillBuilder>): AgentSkill {
        val builder = AgentSkillBuilder()
        block.accept(builder)
        return builder.build()
    }

    /**
     * Configures the provider using the provided configuration block.
     *
     * @param block The lambda to configure the provider.
     */
    public fun provider(block: AgentProviderBuilder.() -> Unit) {
        provider = AgentProviderBuilder().apply(block).build()
    }

    /**
     * Configures the provider using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the provider.
     */
    public fun provider(block: Consumer<AgentProviderBuilder>) {
        val builder = AgentProviderBuilder()
        block.accept(builder)
        provider = builder.build()
    }

    /**
     * Configures the capabilities using the provided configuration block.
     *
     * @param block The lambda to configure the capabilities.
     */
    public fun capabilities(block: AgentCapabilitiesBuilder.() -> Unit) {
        capabilities = AgentCapabilitiesBuilder().apply(block).build()
    }

    /**
     * Configures the capabilities using the provided Java-friendly Consumer.
     *
     * @param block The consumer to configure the capabilities.
     */
    public fun capabilities(block: Consumer<AgentCapabilitiesBuilder>) {
        val builder = AgentCapabilitiesBuilder()
        block.accept(builder)
        capabilities = builder.build()
    }
}

/**
 * Top-level DSL function for creating [AgentCard].
 *
 * @param init The lambda to configure the agent card.
 * @return A new [AgentCard] instance.
 */
public inline fun agentCard(init: AgentCardBuilder.() -> Unit): AgentCard =
    AgentCardBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AgentCard].
 *
 * @param init The consumer to configure the agent card.
 * @return A new [AgentCard] instance.
 */
public fun agentCard(init: Consumer<AgentCardBuilder>): AgentCard {
    val builder = AgentCardBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of an AgentCard using the provided configuration block.
 *
 * @param init A configuration block for building an AgentCard instance using the AgentCardBuilder.
 * @return A newly created AgentCard instance.
 */
public fun AgentCard.Companion.create(init: AgentCardBuilder.() -> Unit): AgentCard =
    AgentCardBuilder().apply(init).build()

/**
 * Creates a new instance of an AgentCard using the provided Java-friendly Consumer.
 *
 * @param init A consumer for building an AgentCard instance using the AgentCardBuilder.
 * @return A newly created AgentCard instance.
 */
public fun AgentCard.Companion.create(init: Consumer<AgentCardBuilder>): AgentCard {
    val builder = AgentCardBuilder()
    init.accept(builder)
    return builder.build()
}
