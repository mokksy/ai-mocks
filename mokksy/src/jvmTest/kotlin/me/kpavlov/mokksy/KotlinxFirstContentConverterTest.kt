package me.kpavlov.mokksy

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.http.ContentType
import io.ktor.http.content.OutputStreamContent
import io.ktor.http.content.TextContent
import io.ktor.serialization.jackson.JacksonConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import me.kpavlov.mokksy.jackson.JacksonInput
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class KotlinxFirstContentConverterTest {
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    private val subject =
        KotlinxFirstContentConverter(
            KotlinxSerializationConverter(
                Json {
                    ignoreUnknownKeys = false
                },
            ),
            JacksonConverter(objectMapper),
        )

    @Test
    fun `serialize uses kotlinx Serialization for eligible classes`() =
        runTest {
            // Arrange
            val typeInfo = typeInfo<TestPerson>()
            val value = TestPerson.random()

            // Act
            val result =
                subject.serialize(
                    ContentType.Application.Json,
                    Charsets.UTF_8,
                    typeInfo,
                    value,
                ) as? TextContent

            // Assert
            result.shouldNotBeNull()
            result.text shouldBe Json.encodeToString(value)
            result.text shouldNotBe objectMapper.writeValueAsString(value)
        }

    @Test
    fun `serialize falls back to fallbackConverter when kotlinxSerializationConverter throws`() =
        runTest {
            // Arrange
            val typeInfo = typeInfo<JacksonInput>()
            val value = JacksonInput("Alice")
            assertFailsWith(SerializationException::class) {
                Json.encodeToString(value)
            }

            // Act
            val result =
                subject.serialize(
                    ContentType.Application.Json,
                    Charsets.UTF_8,
                    typeInfo,
                    value,
                ) as? OutputStreamContent

            // Assert
            result.shouldNotBeNull()
            result.contentType shouldBe ContentType.Application.Json
        }

    @Test
    fun `deserialize uses kotlinx Serialization for eligible classes`() =
        runTest {
            // Arrange
            val typeInfo = typeInfo<TestPerson>()
            val value = TestPerson.random()
            val bytes = Json.encodeToString(value).toByteArray()

            val readChannel = bytes.inputStream().toByteReadChannel()

            // Act
            val result =
                subject.deserialize(
                    Charsets.UTF_8,
                    typeInfo,
                    readChannel,
                ) as? TestPerson

            // Assert
            result.shouldNotBeNull()
            result shouldBeEqual value
        }

    @Test
    fun `deserialize falls back to fallbackConverter when kotlinxSerializationConverter throws`() =
        runTest {
            // Arrange
            val typeInfo = typeInfo<JacksonInput>()
            val value = JacksonInput(name = "Alice")
            val readChannel =
                objectMapper
                    .writeValueAsBytes(
                        value,
                    ).inputStream()
                    .toByteReadChannel()

            // Act
            val result =
                subject.deserialize(
                    Charsets.UTF_8,
                    typeInfo,
                    readChannel,
                ) as? JacksonInput

            // Assert
            result.shouldNotBeNull()
            result shouldBeEqual value
        }
}
