package me.kpavlov.aimocks.a2a.model

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
 * Creates a new instance of an AgentSkill using the provided configuration block.
 *
 * @param block A configuration block for building an AgentSkill instance using the AgentSkillBuilder.
 * @return A newly created AgentSkill instance.
 */
public fun AgentSkill.Companion.create(block: AgentSkillBuilder.() -> Unit): AgentSkill =
    AgentSkillBuilder().apply(block).build()
