package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharsetIfNeeded
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal fun createKtorSSEClient(port: Int): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
        install(SSE) {
            showRetryEvents()
            showCommentEvents()
        }
        install(DefaultRequest) {
            url("http://127.0.0.1:$port") // Set the base URL
        }
    }

internal class MokksySSETest {
    private val mokksy =
        MokksyServer(verbose = true) {
            println("Running server with ${it.engine} engine")
        }

    private val client = createKtorSSEClient(mokksy.port())

    @AfterEach
    fun afterEach() {
        mokksy.checkForUnmatchedRequests()
    }

    @Test
    fun `Should respond to SSE GET`() =
        runTest {
            mokksy.post {
                path = beEqual("/sse")
            } respondsWithSseStream {
                flow =
                    flow {
                        delay(200.milliseconds)
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
            val result = client.post("/sse")

            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.contentType())
                .isEqualTo(ContentType.Text.EventStream.withCharsetIfNeeded(Charsets.UTF_8))
            assertThat(result.bodyAsText()).isEqualTo("data: One\r\ndata: Two\r\n")
        }
}

public suspend fun main() {
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
