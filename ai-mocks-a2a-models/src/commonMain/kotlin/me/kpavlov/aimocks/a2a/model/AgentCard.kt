package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AgentCard(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    @EncodeDefault
    val description: String? = null,
    @SerialName("url")
    val url: String,
    @SerialName("provider")
    @EncodeDefault
    val provider: AgentProvider? = null,
    @SerialName("version")
    val version: String,
    @SerialName("documentationUrl")
    @EncodeDefault
    val documentationUrl: String? = null,
    @SerialName("capabilities")
    val capabilities: AgentCapabilities,
    @SerialName("authentication")
    @EncodeDefault
    val authentication: AgentAuthentication? = null,
    @SerialName("defaultInputModes")
    @EncodeDefault
    val defaultInputModes: List<String> = listOf("text"),
    @SerialName("defaultOutputModes")
    @EncodeDefault
    val defaultOutputModes: List<String> = listOf("text"),
    @SerialName("skills")
    val skills: List<AgentSkill>,
    @SerialName("signatures")
    @EncodeDefault
    val signatures: List<String>? = null,
    @SerialName("supportsAuthenticatedExtendedCard")
    @EncodeDefault
    val supportsAuthenticatedExtendedCard: Boolean? = null,
) {
    public companion object
}
