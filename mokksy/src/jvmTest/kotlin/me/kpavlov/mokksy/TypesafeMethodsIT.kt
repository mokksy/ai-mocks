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
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.fail

@Suppress("UastIncorrectHttpHeaderInspection")
internal class TypesafeMethodsIT : AbstractIT() {
    private lateinit var name: String

    private lateinit var requestPayload: TestPerson

    @BeforeTest
    fun before() {
        name = UUID.randomUUID().toString()

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
            doTestCallMethod<TestPerson>(method) {
                mokksy.method<TestPerson>(name, method, TestPerson::class, it)
            }
        }

    @Test
    fun `Should respond to GET`() =
        runTest {
            doTestCallMethod<TestPerson>(
                HttpMethod.Get,
            ) { mokksy.get<TestPerson>(name, TestPerson::class, it) }
        }

    @Test
    fun `Should respond to OPTIONS`() =
        runTest {
            doTestCallMethod<TestPerson>(
                HttpMethod.Options,
            ) { mokksy.options<TestPerson>(name, TestPerson::class, it) }
        }

    @Test
    fun `Should respond to PUT`() =
        runTest {
            doTestCallMethod<TestPerson>(HttpMethod.Put) {
                mokksy.put<TestPerson>(
                    name,
                    TestPerson::class,
                    it,
                )
            }
        }

    @Test
    fun `Should respond to PATCH`() =
        runTest {
            doTestCallMethod<TestPerson>(HttpMethod.Patch) {
                mokksy.patch<TestPerson>(
                    name,
                    TestPerson::class,
                    it,
                )
            }
        }

    @Test
    fun `Should respond to DELETE`() =
        runTest {
            doTestCallMethod<TestPerson>(HttpMethod.Delete) {
                mokksy.delete<TestPerson>(
                    name,
                    TestPerson::class,
                    it,
                )
            }
        }

    @Test
    fun `Should respond to HEAD`() =
        runTest {
            doTestCallMethod<TestPerson>(HttpMethod.Head) {
                mokksy.head<TestPerson>(
                    name,
                    TestPerson::class,
                    it,
                )
            }
        }

    private suspend fun <P : Any> doTestCallMethod(
        method: HttpMethod,
        block: (RequestSpecificationBuilder<P>.() -> Unit) -> BuildingStep<*>,
    ) {
        val configurer: RequestSpecificationBuilder<P>.() -> Unit = {
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

            try {
                this.request.body shouldBe requestPayload
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
            mokksy
                .get {
                    path = beEqual(uri)
                    this.containsHeader("Foo", "bar")
                }.respondsWith(String::class) {
                    fail("✋🛑 Should not be called")
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

            mokksy
                .post<Input>(name = "post", Input::class) {
                    path = beEqual("/things")
                    bodyContains("$id")
                }.respondsWith(String::class) {
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
                    contentType(ContentType.Application.Json)
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
            result.status shouldBe HttpStatusCode.Created
            result.bodyAsText() shouldBe expectedResponse
            result.headers["Location"] shouldBe "/things/$id"
            result.headers["Foo"] shouldBe "bar"
        }
}
