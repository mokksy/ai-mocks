package me.kpavlov.mokksy.jackson

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
import me.kpavlov.mokksy.AbstractIT
import me.kpavlov.mokksy.MokksyServer
import me.kpavlov.mokksy.ServerConfiguration
import org.junit.jupiter.api.Test

internal class JacksonSerializationIT : AbstractIT() {
    val mokksyWithJackson: MokksyServer =
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
        ) {
            println("Running Mokksy server with ${it.engine} engine")
        }

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
            mokksyWithJackson.post<JacksonInput>(
                requestType = JacksonInput::class,
            ) {
                path = beEqual("/jackson-$seed")
            } respondsWith {
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
