package dev.mokksy.aimocks.a2a.model.serializers

import dev.mokksy.aimocks.a2a.model.TaskState
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

internal class TaskStateSerializerTest {
    private val json =
        Json {
            serializersModule =
                kotlinx.serialization.modules.SerializersModule {
                    contextual(TaskState::class, TaskStateSerializer())
                }
        }

    @Test
    fun testDeserializeKnownValues() {
        json.decodeFromString<TaskState>("\"submitted\"") shouldBe TaskState.SUBMITTED
        json.decodeFromString<TaskState>("\"working\"") shouldBe TaskState.WORKING
        json.decodeFromString<TaskState>("\"input-required\"") shouldBe TaskState.INPUT_REQUIRED
        json.decodeFromString<TaskState>("\"completed\"") shouldBe TaskState.COMPLETED
        json.decodeFromString<TaskState>("\"canceled\"") shouldBe TaskState.CANCELED
        json.decodeFromString<TaskState>("\"failed\"") shouldBe TaskState.FAILED
        json.decodeFromString<TaskState>("\"unknown\"") shouldBe TaskState.UNKNOWN
    }

    @Test
    fun testDeserializeUnknownValue() {
        json.decodeFromString<TaskState>("\"something_else\"") shouldBe TaskState.UNKNOWN
    }
}
