package dev.mokksy.mokksy

import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import dev.mokksy.mokksy.request.RequestSpecificationBuilder
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.BeforeTest

@Suppress("UastIncorrectHttpHeaderInspection")
internal class ShortcutMethodsIT : AbstractIT() {
    private lateinit var name: String

    private lateinit var requestPayload: TestPerson

    @BeforeTest
    fun before() {
        name = UUID.randomUUID().toString()

        requestPayload = TestPerson.random()
    }

    @Test
    fun `Should respond to shortcut GET`() =
        runTest {
            doTestCallMethod(HttpMethod.Get) { mokksy.get(it) }
        }

    @Test
    fun `Should respond to shortcut OPTIONS`() =
        runTest {
            doTestCallMethod(HttpMethod.Options) { mokksy.options(it) }
        }

    @Test
    fun `Should respond to shortcut PUT`() =
        runTest {
            doTestCallMethod(HttpMethod.Put) { mokksy.put(it) }
        }

    @Test
    fun `Should respond to shortcut PATCH`() =
        runTest {
            doTestCallMethod(HttpMethod.Patch) { mokksy.patch(it) }
        }

    @Test
    fun `Should respond to shortcut DELETE`() =
        runTest {
            doTestCallMethod(HttpMethod.Delete) { mokksy.delete(it) }
        }

    @Test
    fun `Should respond to shortcut HEAD`() =
        runTest {
            doTestCallMethod(HttpMethod.Head) { mokksy.head(it) }
        }

    private suspend fun doTestCallMethod(
        method: HttpMethod,
        block: (RequestSpecificationBuilder<String>.() -> Unit) -> BuildingStep<String>,
    ) {
        val configurer: RequestSpecificationBuilder<String>.() -> Unit = {
            path = beEqual("/shortcut-method-$method")
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
                this.request.body shouldBe requestAsString
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
            client.request("/shortcut-method-$method") {
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
    fun `Should respond to shortcut POST`() =
        runTest {
            doTestCallMethod(HttpMethod.Post) { mokksy.post(it) }
        }
}
