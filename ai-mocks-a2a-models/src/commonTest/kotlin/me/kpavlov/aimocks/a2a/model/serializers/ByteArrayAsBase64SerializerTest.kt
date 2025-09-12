package me.kpavlov.aimocks.a2a.model.serializers

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random
import kotlin.test.Test

class ByteArrayAsBase64SerializerTest {
    private val serializer = ByteArrayAsBase64Serializer()
    private val json = Json

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun `should serialize and deserialize bytes`() {
        val data = Random.nextBytes(32)
        val result = json.encodeToString(serializer, data)

        result shouldBe "\"${Base64.encode(data)}\""

        val restored = json.decodeFromString(serializer, result)
        restored shouldBe data
    }
}
