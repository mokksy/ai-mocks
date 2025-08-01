package me.kpavlov.mokksy.utils.logger

import io.ktor.http.ContentType

public object Highlighting {
    /**
     * Applies ANSI color highlighting to an HTTP body string based on its content type.
     *
     * For JSON content, syntax highlighting is applied to keys and values. For form URL-encoded data, keys and values are colored distinctly. Other content types are displayed in light gray.
     *
     * @param body The HTTP body content to highlight.
     * @param contentType The content type of the body.
     * @return The highlighted body string with ANSI color codes.
     */
    public fun highlightBody(body: String, contentType: ContentType): String {
        return when {
            contentType.match(ContentType.Application.Json) -> highlightJson(body)
            contentType.match(ContentType.Application.FormUrlEncoded) -> highlightForm(body)
            else -> colorize(body, AnsiColor.LIGHT_GRAY)
        }
    }

    /**
     * Applies ANSI color highlighting to a JSON string for terminal output.
     *
     * Keys are colored magenta, string values green, numeric values blue, and boolean/null values yellow.
     *
     * @param json The JSON string to highlight.
     * @return The JSON string with ANSI color codes applied for syntax highlighting.
     */
    private fun highlightJson(json: String): String {
        val keyColor = AnsiColor.MAGENTA
        val stringValColor = AnsiColor.GREEN
        val numberValColor = AnsiColor.BLUE
        val boolNullColor = AnsiColor.YELLOW

        val regex = Regex(
            "\"(.*?)\"(\\s*):(\\s*)(\".*?\"|\\d+(\\.\\d+)?|true|false|null)",
            RegexOption.DOT_MATCHES_ALL
        )

        return regex.replace(json) { match ->
            val key = match.groupValues[1]
            val spaceBeforeColon = match.groupValues[2]
            val spaceAfterColon = match.groupValues[3]
            val value = match.groupValues[4]

            val coloredKey = colorize("\"$key\"", keyColor)

            val coloredValue = when {
                value.startsWith("\"") -> colorize(value, stringValColor)
                value == "true" || value == "false" || value == "null" -> colorize(
                    value,
                    boolNullColor
                )

                else -> colorize(value, numberValColor)
            }

            "$coloredKey$spaceBeforeColon:$spaceAfterColon$coloredValue"
        }
    }

    /**
     * Applies ANSI color highlighting to URL-encoded form data.
     *
     * Splits the input string into key-value pairs separated by '&' and colors keys in yellow and values in green.
     * Pairs that do not contain exactly one '=' are left unchanged.
     *
     * @param data The URL-encoded form data to highlight.
     * @return The highlighted form data as a string with ANSI color codes.
     */
    private fun highlightForm(data: String): String {
        return data.split("&").joinToString("&") {
            val parts = it.split("=")
            if (parts.size == 2) {
                val key = colorize(parts[0], AnsiColor.YELLOW)
                val value = colorize(parts[1], AnsiColor.GREEN)
                "$key=$value"
            } else it
        }
    }
}

