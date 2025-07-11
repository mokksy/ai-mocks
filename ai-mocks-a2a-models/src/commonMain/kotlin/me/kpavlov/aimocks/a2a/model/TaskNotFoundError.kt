/*
 * TaskNotFoundError.kt
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
public class TaskNotFoundError : JSONRPCError {
    @JvmOverloads
    public constructor(data: Data? = null) : super(
        code = -32001,
        message = "Task not found",
        data = null,
    )

    public fun copy(data: Data? = this.data): TaskNotFoundError = TaskNotFoundError(data = data)
}
