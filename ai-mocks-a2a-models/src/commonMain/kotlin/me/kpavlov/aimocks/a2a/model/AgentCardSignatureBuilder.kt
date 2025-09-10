package me.kpavlov.aimocks.a2a.model

/**
 * Builder for creating [AgentCardSignature] instances.
 */
public class AgentCardSignatureBuilder {
    public var header: Data? = null
    public var protectedHeader: String? = null
    public var signature: String? = null

    public fun header(header: Data): AgentCardSignatureBuilder = apply { this.header = header }

    public fun header(values: Map<String, Any>): AgentCardSignatureBuilder =
        apply { this.header = Data.of(values) }

    public fun protectedHeader(protectedHeader: String): AgentCardSignatureBuilder =
        apply { this.protectedHeader = protectedHeader }

    public fun signature(signature: String): AgentCardSignatureBuilder =
        apply { this.signature = signature }

    public fun build(): AgentCardSignature {
        val p = requireNotNull(protectedHeader) { "protected header must be set" }
        val s = requireNotNull(signature) { "signature must be set" }
        return AgentCardSignature(
            header = header,
            protectedHeader = p,
            signature = s,
        )
    }
}

public fun agentCardSignature(builder: AgentCardSignatureBuilder.() -> Unit): AgentCardSignature =
    AgentCardSignatureBuilder().apply(builder).build()
