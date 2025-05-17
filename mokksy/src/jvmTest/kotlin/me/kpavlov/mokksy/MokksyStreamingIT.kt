package me.kpavlov.mokksy

import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8
import kotlin.time.Duration.Companion.milliseconds

internal class MokksyStreamingIT : AbstractIT({ createKtorSSEClient(it) }) {
    @Test
    fun `Should respond to stream of Strings (flow)`() =
        runTest {
            mokksy.get {
                path = beEqual("/streaming-flow-$seed")
            } respondsWithStream {
                flow =
                    flow {
                        delay(100.milliseconds)
                        emit("One")
                        delay(50.milliseconds)
                        emit("Two")
                    }
            }

            // when-then
            verifyStream("/streaming-flow-$seed")
        }

    private suspend fun verifyStream(uri: String) {
        // when
        val result = client.get(uri)

        // then
        result.status shouldBe HttpStatusCode.OK
        result.contentType() shouldBe ContentType.Text.EventStream.withCharset(UTF_8)
        result.bodyAsText() shouldBe "OneTwo"
    }

    @Test
    fun `Should respond to stream of Strings (chunks)`() =
        runTest {
            mokksy.get {
                path("/streaming-chunks-$seed")
            } respondsWithStream {
                chunks += "One"
                chunks += "Two"
            }

            // when-then
            verifyStream("/streaming-chunks-$seed")
        }
}
