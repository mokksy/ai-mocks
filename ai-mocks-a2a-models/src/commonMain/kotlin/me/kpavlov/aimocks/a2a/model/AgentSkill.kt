package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentSkill(
    @SerialName("id")
    val id: SkillId,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("examples")
    val examples: List<String>? = null,
    @SerialName("inputModes")
    val inputModes: List<String>? = null,
    @SerialName("outputModes")
    val outputModes: List<String>? = null,
) {
    public companion object {
        /**
         * Creates a new AgentSkill using the DSL builder.
         *
         * @param init The lambda to configure the agent skill.
         * @return A new AgentSkill instance.
         */
        public fun build(init: AgentSkillBuilder.() -> Unit): AgentSkill =
            AgentSkillBuilder().apply(init).build()
    }
}
