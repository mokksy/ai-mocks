package me.kpavlov.mokksy.utils.highlight

internal object JsonHighlighter {
    private val keyColor = AnsiColor.MAGENTA
    private val stringValColor = AnsiColor.GREEN
    private val numberValColor = AnsiColor.BLUE
    private val boolNullColor = AnsiColor.YELLOW

    /**
     * Applies ANSI color highlighting to a JSON string for terminal output.
     *
     * Keys are colored magenta, string values green, numeric values blue, and
     * boolean/null values yellow.
     *
     * @param json The JSON string to highlight.
     * @param useColor Set explicitly should colorize or not
     * @return The JSON string with ANSI color codes applied for syntax highlighting.
     */
    @JvmStatic
    internal fun highlight(
        json: String,
        useColor: Boolean = isColorSupported(),
    ): String {
        if (!useColor) {
            return json
        }

        val state = HighlightContext()
        val result = StringBuilder(json.length + 200)
        var i = 0

        while (i < json.length) {
            val char = json[i]
            processCharacter(char, json, i, state, result)
            i++
        }

        state.flushBuffer(result, null)
        return result.toString()
    }

    private class HighlightContext {
        val buffer = StringBuilder()
        var inString = false
        var isEscaped = false
        var isKey = false
        var afterColon = false

        fun flushBuffer(
            result: StringBuilder,
            color: AnsiColor?,
        ) {
            if (buffer.isNotEmpty()) {
                if (color != null) {
                    result.append(color.code)
                    result.append(buffer)
                    result.append(AnsiColor.RESET.code)
                } else {
                    result.append(buffer)
                }
                buffer.clear()
            }
        }
    }

    private fun processCharacter(
        char: Char,
        json: String,
        position: Int,
        state: HighlightContext,
        result: StringBuilder,
    ) {
        when {
            state.inString -> processStringChar(char, state, result)
            char == '"' -> startString(json, position, state)
            char == ':' -> processColon(state, result, char)
            isStructuralChar(char) -> processStructuralChar(state, result, char)
            char.isWhitespace() -> processWhitespace(state, result, char)
            else -> processValue(char, json, position, state, result)
        }
    }

    private fun processStringChar(
        char: Char,
        state: HighlightContext,
        result: StringBuilder,
    ) {
        state.buffer.append(char)
        when {
            state.isEscaped -> state.isEscaped = false
            char == '\\' -> state.isEscaped = true
            char == '"' -> {
                val color = if (state.isKey) keyColor else stringValColor
                state.flushBuffer(result, color)
                state.inString = false
                state.isKey = false
            }
        }
    }

    private fun startString(
        json: String,
        position: Int,
        state: HighlightContext,
    ) {
        state.inString = true
        state.isKey = !state.afterColon && hasColonAhead(json, position)
        state.buffer.append('"')
    }

    private fun processColon(
        state: HighlightContext,
        result: StringBuilder,
        char: Char,
    ) {
        state.flushBuffer(result, null)
        result.append(char)
        state.afterColon = true
    }

    private fun processStructuralChar(
        state: HighlightContext,
        result: StringBuilder,
        char: Char,
    ) {
        state.flushBuffer(result, null)
        result.append(char)
        state.afterColon = false
    }

    private fun processWhitespace(
        state: HighlightContext,
        result: StringBuilder,
        char: Char,
    ) {
        state.flushBuffer(result, null)
        result.append(char)
    }

    private fun processValue(
        char: Char,
        json: String,
        position: Int,
        state: HighlightContext,
        result: StringBuilder,
    ) {
        state.buffer.append(char)
        val nextChar = if (position + 1 < json.length) json[position + 1] else '\u0000'
        if (isValueTerminator(nextChar)) {
            val color = determineValueColor(state.buffer)
            state.flushBuffer(result, color)
            state.afterColon = false
        }
    }

    /**
     * Efficiently checks if there's a colon ahead (indicating this quote starts a key).
     * Only look ahead until finding ':', newline, or structural char.
     */
    @JvmStatic
    private fun hasColonAhead(
        json: String,
        startPos: Int,
    ): Boolean {
        var pos = startPos + 1
        var inQuotes = true
        var escaped = false

        while (pos < json.length) {
            val c = json[pos]

            when {
                escaped -> escaped = false
                isEscapeChar(c, inQuotes) -> escaped = true
                isQuoteChar(c, escaped) -> inQuotes = !inQuotes
                !inQuotes -> {
                    val result = checkCharAfterString(c)
                    if (result != null) return result
                    // null means whitespace, continue searching
                }
            }
            pos++
        }
        return false
    }

    private fun isEscapeChar(
        c: Char,
        inQuotes: Boolean,
    ): Boolean = c == '\\' && inQuotes

    private fun isQuoteChar(
        c: Char,
        escaped: Boolean,
    ): Boolean = c == '"' && !escaped

    private fun checkCharAfterString(c: Char): Boolean? =
        when (c) {
            ':' -> true
            '{', '}', '[', ']', ',' -> false
            ' ', '\t', '\n', '\r' -> null // Continue searching through whitespace
            else -> false
        }

    private fun isStructuralChar(c: Char): Boolean =
        c == '{' || c == '}' || c == '[' || c == ']' || c == ','

    private fun isValueTerminator(c: Char): Boolean =
        c == ',' || c == '}' || c == ']' || c == ' ' ||
            c == '\t' || c == '\n' || c == '\r' || c == '\u0000'

    /**
     * Determines value color without regex - checks if it's a number or boolean/null.
     */
    @JvmStatic
    private fun determineValueColor(buffer: StringBuilder): AnsiColor? {
        if (buffer.isEmpty()) return null

        val first = buffer[0]

        // Check for boolean/null keywords first
        when (buffer.length) {
            4 -> {
                if (buffer.contentEquals("true") || buffer.contentEquals("null")) {
                    return boolNullColor
                }
            }

            5 -> {
                if (buffer.contentEquals("false")) {
                    return boolNullColor
                }
            }
        }

        // Check if it's a number
        return if (isNumber(first, buffer)) numberValColor else null
    }

    @JvmStatic
    private fun isNumber(
        firstChar: Char,
        buffer: StringBuilder,
    ): Boolean {
        if (!isValidNumberStart(firstChar)) return false

        var hasDigit = false
        var hasDot = false
        var hasExp = false

        for (i in 0 until buffer.length) {
            val char = buffer[i]
            when (char) {
                '-', '+' -> {
                    if (i == 0) continue
                    val prevChar = buffer[i - 1]
                    if (prevChar != 'e' && prevChar != 'E') return false
                }
                '.' -> {
                    if (hasDot || hasExp) return false
                    hasDot = true
                }
                'e', 'E' -> {
                    if (hasExp || !hasDigit) return false
                    hasExp = true
                }
                in '0'..'9' -> hasDigit = true
                else -> return false
            }
        }
        return hasDigit
    }

    private fun isValidNumberStart(c: Char): Boolean = c == '-' || c.isDigit()

    @JvmStatic
    private fun StringBuilder.contentEquals(str: String): Boolean {
        if (this.length != str.length) return false
        for (i in 0 until length) {
            if (this[i] != str[i]) return false
        }
        return true
    }
}
