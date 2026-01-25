package dev.mokksy.mokksy.utils.highlight

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.http.ContentType
import kotlin.test.Test

internal class HighlightingTest {
    @Test
    fun `should highlight form parameters with correct colors and preserve format`() {
        val input = "name=Alice&age=30&debug=true"
        val result =
            Highlighting.highlightBody(
                body = input,
                contentType = ContentType.Application.FormUrlEncoded,
                useColor = true,
            )

        assertSoftly {
            result shouldContain colorize("name", AnsiColor.YELLOW)
            result shouldContain colorize("Alice", AnsiColor.GREEN)

            result shouldContain colorize("age", AnsiColor.YELLOW)
            result shouldContain colorize("30", AnsiColor.GREEN)

            result shouldContain colorize("debug", AnsiColor.YELLOW)
            result shouldContain colorize("true", AnsiColor.GREEN)

            result.count { it == '&' } shouldBe 2
        }
    }

    @Test
    fun `should leave invalid pairs untouched`() {
        val input = "incomplete&validKey=value"
        val result =
            Highlighting.highlightBody(
                body = input,
                contentType = ContentType.Application.FormUrlEncoded,
                useColor = true,
            )

        assertSoftly {
            result shouldContain "incomplete"
            result shouldContain colorize("validKey", AnsiColor.YELLOW)
            result shouldContain colorize("value", AnsiColor.GREEN)
        }
    }

    @Test
    fun `should highlight JSON key-value pairs with correct colors and retain spaces`() {
        // language=json
        val input =
            """
            {
                "name"  :  "Alice",
                "age":42,
                "active" : true,
                "nickname" : null
            }
            """.trimIndent()

        val result =
            Highlighting.highlightBody(
                body = input,
                contentType = ContentType.Application.Json,
                useColor = true,
            )

        assertSoftly {
            result shouldContain colorize("\"name\"", AnsiColor.MAGENTA)
            result shouldContain colorize("\"Alice\"", AnsiColor.GREEN)

            result shouldContain colorize("\"age\"", AnsiColor.MAGENTA)
            result shouldContain colorize("42", AnsiColor.BLUE)

            result shouldContain colorize("true", AnsiColor.YELLOW)

            result shouldContain colorize("null", AnsiColor.YELLOW)

            // Check spacing is retained (e.g., double space before/after colon in "name")
            result shouldContain "name\"\u001B[0m  :  "
        }
    }

    private fun colorize(
        text: String,
        color: AnsiColor,
    ): String = "${color.code}$text\u001B[0m"
}
