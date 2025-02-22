package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import me.kpavlov.mokksy.request.RequestSpecificationBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.fail
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("UastIncorrectHttpHeaderInspection")
internal class MokksyIT : AbstractIT() {
    private lateinit var name: String

    private lateinit var requestPayload: TestPerson

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun before() {
        name = Uuid.random().toString()

        requestPayload = TestPerson.random()
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
        val configurer: RequestSpecificationBuilder<*>.() -> Unit = {
            path = beEqual("/method-$method")
            this.containsHeader("X-Seed", "$seed")
        }

        val expectedResponseRef = AtomicReference<String>()
        val requestAsString = Json.encodeToString(requestPayload)

        block.invoke {
            configurer(this)
        } respondsWith {
            try {
                this.request.bodyAsString shouldBe requestAsString
            } catch (e: AssertionError) {
                logger.error(e) { "Request bodyAsString does not match." }
                throw e
            }

            val responsePayload = TestOrder.random(person = requestPayload)
            body = Json.encodeToString(responsePayload)
            expectedResponseRef.set(body) // safely store the response for verification
        }

        // when
        val result =
            client.request("/method-$method") {
                this.method = method
                headers.append("X-Seed", "$seed")
                contentType(ContentType.Application.Json)
                setBody(requestAsString)
            }

        // then
        result.status shouldBe HttpStatusCode.OK
        result.status shouldBe HttpStatusCode.OK

        if (method != HttpMethod.Head) {
            result.bodyAsText() shouldBe expectedResponseRef.get()
        } else {
            result.bodyAsText() shouldBe ""
        }
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
                fail("✋🛑 Should not be called")
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
