package dev.mokksy.mokksy.request

import dev.mokksy.mokksy.CapturedRequest
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.server.response.respondText
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import io.ktor.server.routing.post as serverPost

internal class CapturedRequestTest {
    @Test
    fun `should cache typed body under concurrent access`() =
        runTest {
            val raw = "concurrent-body-text"

            testApplication {
                routing {
                    serverPost("/body") {
                        val captured = CapturedRequest(call.request, String::class)

                        val first = captured.body

                        val allSame =
                            coroutineScope {
                                val results = (1..25).map { async { captured.body } }.awaitAll()
                                results.all { it == first }
                            }

                        call.respondText("$first|$allSame")
                    }
                }

                val response =
                    client.post("/body") {
                        contentType(ContentType.Text.Plain)
                        setBody(raw)
                    }

                val payload = response.bodyAsText()
                val parts = payload.split("|")

                // First part is the returned body, second part is the allSame flag
                parts[0] shouldBe raw
                parts[1] shouldBe "true"
            }
        }

    @Test
    fun `should cache bodyAsString under concurrent access`() =
        runTest {
            val raw = "concurrent-body-string"

            testApplication {
                routing {
                    serverPost("/bodyString") {
                        val captured = CapturedRequest(call.request, String::class)

                        val first = captured.bodyAsString

                        val allSame =
                            coroutineScope {
                                val results =
                                    (1..25)
                                        .map { async { captured.bodyAsString } }
                                        .awaitAll()
                                results.all { it == first }
                            }

                        call.respondText("${first.orEmpty()}|$allSame")
                    }
                }

                val response =
                    client.post("/bodyString") {
                        contentType(ContentType.Text.Plain)
                        setBody(raw)
                    }

                val payload = response.bodyAsText()
                val parts = payload.split("|")

                parts[0] shouldBe raw
                parts[1] shouldBe "true"
            }
        }
}
