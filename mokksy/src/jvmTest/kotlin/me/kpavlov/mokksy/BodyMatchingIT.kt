package me.kpavlov.mokksy

import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.random.Random
import kotlin.test.BeforeTest

@Suppress("UastIncorrectHttpHeaderInspection")
internal class BodyMatchingIT : AbstractIT() {
    private lateinit var name: String

    private lateinit var requestPayload: TestPerson

    @BeforeTest
    fun before() {
        name = UUID.randomUUID().toString()

        requestPayload = TestPerson.random()
    }

    @Test
    fun `Should match body predicate`() =
        runTest {
            // given
            val id = Random.nextInt().toString()
            val expectedResponse = TestPerson.random()

            mokksy
                .post(name = "predicate", Input::class) {
                    path("/predicate")

                    bodyMatchesPredicate {
                        it?.name?.contains(id) == true
                    }

                    bodyMatchesPredicates({
                        it?.name?.isNotBlank() == true
                    })
                }.respondsWith(TestPerson::class) {
                    body = expectedResponse
                    httpStatus = HttpStatusCode.Created
                    headers += "Foo" to "bar" // list style
                }
            // when
            val result =
                client.post("/predicate") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(Input(id)))
                }

            // then
            result.status shouldBe HttpStatusCode.Created
            result.bodyAsText() shouldBe Json.encodeToString(expectedResponse)
            result.headers["Foo"] shouldBe "bar"
        }
}
