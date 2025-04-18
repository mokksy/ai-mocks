package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentCard(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("provider")
    val provider: AgentProvider? = null,
    @SerialName("version")
    val version: String,
    @SerialName("documentationUrl")
    val documentationUrl: String? = null,
    @SerialName("capabilities")
    val capabilities: AgentCapabilities,
    @SerialName("authentication")
    val authentication: AgentAuthentication? = null,
    @SerialName("defaultInputModes")
    val defaultInputModes: List<String> = listOf("text"),
    @SerialName("defaultOutputModes")
    val defaultOutputModes: List<String> = listOf("text"),
    @SerialName("skills")
    val skills: List<AgentSkill>,
) {
    public companion object
}
