package dev.mokksy.mokksy.jackson

import dev.mokksy.mokksy.AbstractIT
import dev.mokksy.mokksy.MokksyServer
import dev.mokksy.mokksy.ServerConfiguration
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

private val mokksyWithJackson: MokksyServer =
    MokksyServer(
        configuration =
            ServerConfiguration(
                verbose = true,
                contentNegotiationConfigurer = {
                    it.jackson {
                        findAndRegisterModules()
                    }
                },
            ),
    )

internal class JacksonSerializationIT : AbstractIT() {
    private val jacksonClient =
        HttpClient(Java) {
            install(ContentNegotiation) {
                jackson()
            }
            install(DefaultRequest) {
                url("http://127.0.0.1:${mokksyWithJackson.port()}") // Set the base URL
            }
        }

    @Test
    fun `Should respond to POST with Jackson`() =
        runTest {
            mokksyWithJackson
                .post(
                    requestType = JacksonInput::class,
                ) {
                    path = beEqual("/jackson-$seed")
                }.respondsWith(JacksonOutput::class) {
                    body = JacksonOutput("Hello, ${request.body.name}")
                }

            val result =
                jacksonClient.post("/jackson-$seed") {
                    contentType(ContentType.Application.Json)
                    setBody(JacksonInput("Bob"))
                }
            result.status shouldBe HttpStatusCode.OK

            result.bodyAsText() shouldBe
                // language=json
                """
                {"pikka-hi":"Hello, Bob"}
                """.trimIndent()
        }
}
