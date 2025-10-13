package dev.mokksy.mokksy.utils.highlight

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

private const val RESET = "\u001B[0m"
private const val MAGENTA = "\u001B[35m"
private const val GREEN = "\u001B[32m"
private const val BLUE = "\u001B[34m"
private const val YELLOW = "\u001B[33m"

@Suppress("MaxLineLength")
class JsonHighlighterTest {
    @Test
    fun `Should highlight simple Json`() {
        // language=json
        val input = """{"foo": "bar", "ba\"z": 1}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe
            "{$MAGENTA\"foo\"$RESET: $GREEN\"bar\"$RESET, $MAGENTA\"ba\\\"z\"$RESET: ${BLUE}1$RESET}"
    }

    @Test
    fun `Should highlight Json with newline`() {
        // language=json
        val input = "{\n  \"foo\"\n  : \n  \"bar\"\n}"
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe
            "{\n  $MAGENTA\"foo\"$RESET\n  : \n  $GREEN\"bar\"$RESET\n}"
    }

    @Test
    fun `Should highlight nested Json`() {
        // language=json
        val input = """{"foo": {"bar": "ba\"z"}}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe "{$MAGENTA\"foo\"$RESET: {$MAGENTA\"bar\"$RESET: $GREEN\"ba\\\"z\"$RESET}}"
    }

    @Test
    fun `Should highlight Json with array`() {
        // language=json
        val input = """{"foo": ["bar", "ba\"z"]}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe
            "{$MAGENTA\"foo\"$RESET: [$GREEN\"bar\"$RESET, $GREEN\"ba\\\"z\"$RESET]}"
    }

    @Test
    fun `Should highlight Json with array of objects`() {
        // language=json
        val input = """{"foo": [{"bar": "baz"}]}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe
            "{$MAGENTA\"foo\"$RESET: [{$MAGENTA\"bar\"$RESET: $GREEN\"baz\"$RESET}]}"
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "42",
            "-42",
            "0",
            "3.14",
            "-3.14",
            "0.5",
            "1.5e10",
            "1.5E10",
            "-1.5e10",
            "2.5e-5",
            "-2.5E-5",
        ],
    )
    fun `Should highlight different number types`(numberValue: String) {
        // language=json
        val input = """{"number": $numberValue}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe "{$MAGENTA\"number\"$RESET: $BLUE$numberValue$RESET}"
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `Should highlight boolean value`(value: Boolean) {
        // language=json
        val input = """{"flag": $value}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe "{$MAGENTA\"flag\"$RESET: ${YELLOW}$value$RESET}"
    }

    @Test
    fun `Should highlight null value`() {
        // language=json
        val input = """{"value": null}"""
        val result = JsonHighlighter.highlight(input, useColor = true)
        // language=text
        result shouldBe "{$MAGENTA\"value\"$RESET: ${YELLOW}null$RESET}"
    }

    @ParameterizedTest
    @CsvSource(
        "true, true",
        "false, false",
        "null, null",
    )
    fun `Should highlight boolean and null values in arrays`(
        value1: String,
        value2: String,
    ) {
        // language=json
        val input = """{"values": [$value1, $value1]}"""
        val result = JsonHighlighter.highlight(json = input, useColor = true)
        // language=text
        result shouldBe "{$MAGENTA\"values\"$RESET: [$YELLOW$value2$RESET, $YELLOW$value2$RESET]}"
    }
}
