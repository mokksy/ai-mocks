package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecificationBuilder
import io.kotest.matchers.shouldBe
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import kotlinx.serialization.json.Json
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
    suspend fun `Should respond to shortcut GET`() {
        doTestCallMethod(HttpMethod.Get) { mokksy.get(it) }
    }

    @Test
    suspend fun `Should respond to shortcut OPTIONS`() {
        doTestCallMethod(HttpMethod.Options) { mokksy.options(it) }
    }

    @Test
    suspend fun `Should respond to shortcut PUT`() {
        doTestCallMethod(HttpMethod.Put) { mokksy.put(it) }
    }

    @Test
    suspend fun `Should respond to shortcut PATCH`() {
        doTestCallMethod(HttpMethod.Patch) { mokksy.patch(it) }
    }

    @Test
    suspend fun `Should respond to shortcut DELETE`() {
        doTestCallMethod(HttpMethod.Delete) { mokksy.delete(it) }
    }

    @Test
    suspend fun `Should respond to shortcut HEAD`() {
        doTestCallMethod(HttpMethod.Head) { mokksy.head(it) }
    }

    @Test
    suspend fun `Should respond to shortcut POST`() {
        doTestCallMethod(HttpMethod.Post) { mokksy.post(it) }
    }

    private suspend fun doTestCallMethod(
        method: HttpMethod,
        block: (RequestSpecificationBuilder<String>.() -> Unit) -> BuildingStep<String>,
    ) {
        val configurer: RequestSpecificationBuilder<String>.() -> Unit = {
            path("/shortcut-method-$method")
            this.containsHeader("X-Seed", "$seed")
        }

        val expectedResponseRef = AtomicReference<String>()
        val requestAsString = Json.encodeToString(requestPayload)
        val expectedContentType = ContentType.Application.ProblemJson.withCharset(Charsets.UTF_32)
        val capturedError = AtomicReference<Throwable?>()

        block.invoke {
            configurer(this)
        } respondsWith {
            try {
                this.request.bodyAsString() shouldBe requestAsString
                this.request.body() shouldBe requestAsString
            } catch (e: AssertionError) {
                capturedError.set(e)
            }

            val responsePayload = TestOrder.random(person = requestPayload)
            contentType = expectedContentType
            body = Json.encodeToString(responsePayload)
            expectedResponseRef.set(body)
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
        capturedError.get()?.let { throw it }
        result.status shouldBe HttpStatusCode.OK
        result.contentType() shouldBe expectedContentType

        if (method != HttpMethod.Head) {
            result.bodyAsText() shouldBe expectedResponseRef.get()
        } else {
            result.bodyAsText() shouldBe ""
        }
    }
}
