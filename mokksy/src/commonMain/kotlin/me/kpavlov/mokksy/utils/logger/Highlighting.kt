package me.kpavlov.mokksy.utils.logger

import io.ktor.http.ContentType

public object Highlighting {
    public fun highlightBody(body: String, contentType: ContentType): String {
        return when {
            contentType.match(ContentType.Application.Json) -> highlightJson(body)
            contentType.match(ContentType.Application.FormUrlEncoded) -> highlightForm(body)
            else -> colorize(body, AnsiColor.LIGHT_GRAY)
        }
    }

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

