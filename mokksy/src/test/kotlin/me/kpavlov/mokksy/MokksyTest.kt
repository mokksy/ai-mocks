package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal fun createKtorClient(port: Int): HttpClient =
    HttpClient(Java) {
        install(ContentNegotiation) {
            Json {
                // Configure JSON serialization
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
        install(DefaultRequest) {
            url("http://127.0.0.1:$port") // Set the base URL
        }
    }

internal class MokksyTest {
    private val mokksy =
        MokksyServer {
            println("Running server with ${it.engine} engine")
        }

    private val client = createKtorClient(mokksy.port())

    @AfterEach
    fun afterEach() {
        mokksy.checkForUnmatchedRequests()
    }

    @Test
    fun `Should respond to GET`() =
        runTest {
            val expectedResponse =
                // language=json
                """
                {
                    "response": "Pong"
                }
                """.trimIndent()
            mokksy.get {
                path = beEqual("/ping")
            } respondsWith {
                body = expectedResponse
            }

            // when
            val result = client.get("/ping")

            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.bodyAsText()).isEqualTo(expectedResponse)
        }

    @Test
    fun `Should respond 404 to unknown request`() =
        runTest {
            // when
            val result = client.get("/unknown")

            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.NotFound)
        }

    @Test
    fun `Should respond to POST`() =
        runTest {
            // given
            val id = Random.nextInt()
            val expectedResponse =
                // language=json
                """
                {
                    "id": "$id",
                    "name": "thing-$id"
                }
                """.trimIndent()

            mokksy.post {
                path = beEqual("/things")
                bodyContains("\"$id\"")
            } respondsWith {
                body = expectedResponse
                httpStatus = HttpStatusCode.Created
                headers {
                    // type-safe builder style
                    append(HttpHeaders.Location, "/things/$id")
                }
                headers += "Foo" to "bar" // list style
            }

            // when
            val result =
                client.post("/things") {
                    headers.append("Content-Type", "application/json")
                    setBody(
                        // language=json
                        """
                        {
                            "id": "$id"
                        }
                        """.trimIndent(),
                    )
                }

            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.Created)
            assertThat(result.bodyAsText()).isEqualTo(expectedResponse)
            assertThat(result.headers["Location"]).isEqualTo("/things/$id")
            assertThat(result.headers["Foo"]).isEqualTo("bar")
        }
}
