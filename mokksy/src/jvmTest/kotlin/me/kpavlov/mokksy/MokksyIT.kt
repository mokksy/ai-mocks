package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("UastIncorrectHttpHeaderInspection")
internal class MokksyIT : AbstractIT() {
    private lateinit var name: String

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()
    }

    @ParameterizedTest()
    @ValueSource(
        strings = [
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS",
        ],
    )
    fun `Should respond to Method`(methodName: String) =
        runTest {
            val method = HttpMethod.parse(methodName)
            doTestCallMethod(method) {
                mokksy.method<Any>(name, method, it)
            }
        }

    @Test
    fun `Should respond to GET`() =
        runTest {
            doTestCallMethod(HttpMethod.Get) { mokksy.get<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut GET`() =
        runTest {
            doTestCallMethod(HttpMethod.Get) { mokksy.get(it) }
        }

    @Test
    fun `Should respond to OPTIONS`() =
        runTest {
            doTestCallMethod(HttpMethod.Options) { mokksy.options<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut OPTIONS`() =
        runTest {
            doTestCallMethod(HttpMethod.Options) { mokksy.options(it) }
        }

    @Test
    fun `Should respond to PUT`() =
        runTest {
            doTestCallMethod(HttpMethod.Put) { mokksy.put<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut PUT`() =
        runTest {
            doTestCallMethod(HttpMethod.Put) { mokksy.put(it) }
        }

    @Test
    fun `Should respond to PATCH`() =
        runTest {
            doTestCallMethod(HttpMethod.Patch) { mokksy.patch<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut PATCH`() =
        runTest {
            doTestCallMethod(HttpMethod.Patch) { mokksy.patch(it) }
        }

    @Test
    fun `Should respond to DELETE`() =
        runTest {
            doTestCallMethod(HttpMethod.Delete) { mokksy.delete<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut DELETE`() =
        runTest {
            doTestCallMethod(HttpMethod.Delete) { mokksy.delete(it) }
        }

    @Test
    fun `Should respond to HEAD`() =
        runTest {
            doTestCallMethod(HttpMethod.Head) { mokksy.head<Any>(name, it) }
        }

    @Test
    fun `Should respond to shortcut HEAD`() =
        runTest {
            doTestCallMethod(HttpMethod.Head) { mokksy.head(it) }
        }

    private suspend fun doTestCallMethod(
        method: HttpMethod,
        block: (RequestSpecificationBuilder<*>.() -> Unit) -> BuildingStep<*>,
    ) {
        val expectedResponse =
            // language=json
            """
            {
                "response": "Pong"
            }
            """.trimIndent()
        val configurer: RequestSpecificationBuilder<*>.() -> Unit = {
            path = beEqual("/method-$method")
            this.containsHeader("X-Seed", "$seed")
        }
        block.invoke {
            configurer(this)
        } respondsWith {
            body = expectedResponse
        }

        // when
        val result =
            client.request("/method-$method") {
                this.method = method
                this.headers.append("X-Seed", "$seed")
            }

        // then
        assertThat(result.status).isEqualTo(HttpStatusCode.OK)
        assertThat(result.bodyAsText()).isEqualTo(
            if (method != HttpMethod.Head) {
                expectedResponse
            } else {
                ""
            },
        )
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
    fun `Should respond 404 to unmatched headers`() =
        runTest {
            val uri = "/unmatched-headers"
            mokksy.get<Any> {
                path = beEqual(uri)
                this.containsHeader("Foo", "bar")
            } respondsWith {
                httpStatus = HttpStatusCode.OK
                body = "Hello"
            }
            // when
            val result = client.get(uri)

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

            mokksy.post<Input>(name = "post") {
                path = beEqual("/things")
                bodyContains("$id")
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
                            "name": "the thing: $id"
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

    @Test
    fun `Should respond to shortcut POST`() =
        runTest {
            doTestCallMethod(HttpMethod.Post) { mokksy.post(it) }
        }
}
