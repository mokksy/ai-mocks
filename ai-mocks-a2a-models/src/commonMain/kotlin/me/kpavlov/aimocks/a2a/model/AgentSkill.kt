package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a distinct capability or function that an agent can perform.
 */
@Serializable
public data class AgentSkill(
    /**
     * A unique identifier for the agent's skill.
     */
    @SerialName("id")
    val id: String,

    /**
     * A human-readable name for the skill.
     */
    @SerialName("name")
    val name: String,

    /**
     * A detailed description of the skill, intended to help clients or users
     * understand its purpose and functionality.
     */
    @SerialName("description")
    val description: String,

    /**
     * A set of keywords describing the skill's capabilities.
     *
     * @sample ["cooking", "customer support", "billing"]
     */
    @SerialName("tags")
    val tags: List<String>,

    /**
     * Example prompts or scenarios that this skill can handle. Provides a hint to
     * the client on how to use the skill.
     *
     * @sample ["I need a recipe for bread"]
     */
    @SerialName("examples")
    val examples: List<String>? = null,

    /**
     * The set of supported input MIME types for this skill, overriding the agent's defaults.
     */
    @SerialName("inputModes")
    val inputModes: List<String>? = null,

    /**
     * The set of supported output MIME types for this skill, overriding the agent's defaults.
     */
    @SerialName("outputModes")
    val outputModes: List<String>? = null,

    /**
     * Security schemes necessary for the agent to leverage this skill.
     * As in the overall AgentCard.security, this list represents a logical OR of security
     * requirement objects. Each object is a set of security schemes that must be used together
     * (a logical AND).
     *
     * @sample
     * ```
     * [
     *   {
     *     "google": [ "oidc" ]
     *   }
     * ]
     * ```
     */
    @SerialName("security")
    val security: List<Map<String, List<String>>>? = null,
) {
    public companion object {
        /**
         * Creates a new AgentSkill using the DSL builder.
         *
         * @param init The lambda to configure the agent skill.
         * @return A new AgentSkill instance.
         */
        @JvmStatic
        public fun build(init: AgentSkillBuilder.() -> Unit): AgentSkill =
            AgentSkillBuilder().apply(init).build()
    }
}
