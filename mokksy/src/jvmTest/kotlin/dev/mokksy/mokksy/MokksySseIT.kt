package dev.mokksy.mokksy

import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.sse.ServerSentEvent
import io.ktor.sse.TypedServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8
import kotlin.time.Duration.Companion.milliseconds

internal class MokksySseIT : AbstractIT({ createKtorSSEClient(it) }) {
    @Test
    fun `Should respond to SSE (flow)`() =
        runTest {
            mokksy.get(name = "sse-get-flow", requestType = Any::class) {
                path("/sse-flow")
            } respondsWithSseStream {
                flow =
                    flow {
                        delay(100.milliseconds)
                        emit(
                            ServerSentEvent(
                                data = "One",
                            ),
                        )
                        delay(50.milliseconds)
                        emit(
                            ServerSentEvent(
                                data = "Two",
                            ),
                        )
                    }
            }

            // when
            verifySseStream("/sse-flow")
        }

    @Test
    fun `Should respond to SSE (chunks)`() =
        runTest {
            mokksy.get(name = "sse-get-chunks", requestType = Any::class) {
                path = beEqual("/sse-chunks")
            } respondsWithSseStream {
                chunks += ServerSentEvent(data = "One")
                chunks += ServerSentEvent(data = "Two")
            }

            // when
            verifySseStream("/sse-chunks")
        }

    private suspend fun verifySseStream(uri: String) {
        val result = client.get(uri)

        // then
        result.status shouldBe HttpStatusCode.OK
        result.contentType() shouldBe ContentType.Text.EventStream.withCharset(UTF_8)
        result.bodyAsText() shouldBe "data: One\r\ndata: Two\r\n"
    }
}

suspend fun main() {
    val mokksy =
        MokksyServer(verbose = true, port = 8080) {
            println("Running server with ${it.engine} engine")
        }
    mokksy.get {
        path = beEqual("/sse")
    } respondsWithSseStream {
        this.httpStatus = HttpStatusCode.OK
        this.flow =
            flow {
                repeat(10) {
                    emit(
                        TypedServerSentEvent(
                            data = "Event $it",
                        ),
                    )
                }
            }
    }

    delay(1000_000L)
}
