package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder class for creating [AgentSkill] instances.
 *
 * This builder provides a fluent API for creating AgentSkill objects,
 * making it easier to configure agent skills.
 *
 * Example usage:
 * ```kotlin
 * val skill = AgentSkillBuilder()
 *     .id("skill-123")
 *     .name("Example Skill")
 *     .description("This is an example skill")
 *     .tags(listOf("example", "demo"))
 *     .examples(listOf("Example usage 1", "Example usage 2"))
 *     .inputModes(listOf("text"))
 *     .outputModes(listOf("text"))
 *     .create()
 * ```
 */
public class AgentSkillBuilder {
    public var id: String? = null
    public var name: String? = null
    public var description: String? = null
    public var tags: List<String>? = null
    public var examples: List<String>? = null
    public var inputModes: List<String>? = null
    public var outputModes: List<String>? = null

    /**
     * Sets the ID of the skill.
     *
     * @param id The ID of the skill.
     * @return This builder instance for method chaining.
     */
    public fun id(id: String): AgentSkillBuilder =
        apply {
            this.id = id
        }

    /**
     * Sets the name of the skill.
     *
     * @param name The name of the skill.
     * @return This builder instance for method chaining.
     */
    public fun name(name: String): AgentSkillBuilder =
        apply {
            this.name = name
        }

    /**
     * Sets the description of the skill.
     *
     * @param description The description of the skill.
     * @return This builder instance for method chaining.
     */
    public fun description(description: String): AgentSkillBuilder =
        apply {
            this.description = description
        }

    /**
     * Sets the tags of the skill.
     *
     * @param tags The tags of the skill.
     * @return This builder instance for method chaining.
     */
    public fun tags(tags: List<String>): AgentSkillBuilder =
        apply {
            this.tags = tags
        }

    /**
     * Sets the examples of the skill.
     *
     * @param examples The examples of the skill.
     * @return This builder instance for method chaining.
     */
    public fun examples(examples: List<String>): AgentSkillBuilder =
        apply {
            this.examples = examples
        }

    /**
     * Sets the input modes of the skill.
     *
     * @param inputModes The input modes of the skill.
     * @return This builder instance for method chaining.
     */
    public fun inputModes(inputModes: List<String>): AgentSkillBuilder =
        apply {
            this.inputModes = inputModes
        }

    /**
     * Sets the output modes of the skill.
     *
     * @param outputModes The output modes of the skill.
     * @return This builder instance for method chaining.
     */
    public fun outputModes(outputModes: List<String>): AgentSkillBuilder =
        apply {
            this.outputModes = outputModes
        }

    /**
     * Builds an [AgentSkill] instance with the configured parameters.
     *
     * @return A new [AgentSkill] instance.
     * @throws IllegalArgumentException If required parameters are missing.
     */
    public fun build(): AgentSkill {
        requireNotNull(id) { "Skill ID is required" }
        requireNotNull(name) { "Skill name is required" }

        return AgentSkill(
            id = id!!,
            name = name!!,
            description = description,
            tags = tags,
            examples = examples,
            inputModes = inputModes,
            outputModes = outputModes,
        )
    }
}

/**
 * Top-level DSL function for creating [AgentSkill].
 *
 * @param init The lambda to configure the agent skill.
 * @return A new [AgentSkill] instance.
 */
public inline fun agentSkill(init: AgentSkillBuilder.() -> Unit): AgentSkill =
    AgentSkillBuilder().apply(init).build()

/**
 * Java-friendly top-level DSL function for creating [AgentSkill].
 *
 * @param init The consumer to configure the agent skill.
 * @return A new [AgentSkill] instance.
 */
public fun agentSkill(init: Consumer<AgentSkillBuilder>): AgentSkill {
    val builder = AgentSkillBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new instance of an AgentSkill using the provided configuration block.
 *
 * @param block A configuration block for building an AgentSkill instance using the AgentSkillBuilder.
 * @return A newly created AgentSkill instance.
 */
public fun AgentSkill.Companion.create(block: AgentSkillBuilder.() -> Unit): AgentSkill =
    AgentSkillBuilder().apply(block).build()

/**
 * Creates a new instance of an AgentSkill using the provided Java-friendly Consumer.
 *
 * @param block A consumer for building an AgentSkill instance using the AgentSkillBuilder.
 * @return A newly created AgentSkill instance.
 */
public fun AgentSkill.Companion.create(block: Consumer<AgentSkillBuilder>): AgentSkill {
    val builder = AgentSkillBuilder()
    block.accept(builder)
    return builder.build()
}
