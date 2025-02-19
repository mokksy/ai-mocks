package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharsetIfNeeded
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class MokksySSEIT : AbstractIT({ createKtorSSEClient(it) }) {
    @Test
    fun `Should respond to SSE GET`() =
        runTest {
            mokksy.get<Any>(name = "sse-get") {
                path = beEqual("/sse")
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
            val result = client.get("/sse")

            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.contentType())
                .isEqualTo(ContentType.Text.EventStream.withCharsetIfNeeded(Charsets.UTF_8))
            assertThat(result.bodyAsText()).isEqualTo("data: One\r\ndata: Two\r\n")
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
                        ServerSentEvent(
                            data = "Event $it",
                        ),
                    )
                }
            }
    }

    delay(1000_000L)
}
