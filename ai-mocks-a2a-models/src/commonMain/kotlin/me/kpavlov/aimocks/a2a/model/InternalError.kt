/*
 * InternalError.kt
 *
 * This code was generated by json-kotlin-schema-codegen - JSON Schema Code Generator
 * See https://github.com/pwall567/json-kotlin-schema-codegen
 *
 * It is not advisable to modify generated code as any modifications will be lost
 * when the generation process is re-run.
 *
 * This was generated from A2A Schema: https://raw.githubusercontent.com/google/A2A/refs/heads/main/specification/json/a2a.json
 */
package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable

@Serializable
public class InternalError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = -32603,
        message = "Internal error",
        data = data,
    )

    public fun copy(data: Data? = this.data): InternalError = InternalError(data = data)
}
