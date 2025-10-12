package me.kpavlov.mokksy.utils.highlight

internal object FormParamsHighlighter {
    /**
     * Applies ANSI color highlighting to URL-encoded form data.
     *
     * Splits the input string into key-value pairs separated by '&' and colors keys in yellow and values in green.
     * Pairs that do not contain exactly one '=' are left unchanged.
     *
     * @param data The URL-encoded form data to highlight.
     * @param useColor Set explicitly should colorize or not
     * @return The highlighted form data as a string with ANSI color codes.
     */
    internal fun highlight(
        data: String,
        useColor: Boolean = isColorSupported(),
    ): String =
        data.split('&').joinToString("&") {
            val parts = it.split('=', limit = 2)
            if (parts.size == 2) {
                val key = parts[0].colorize(AnsiColor.YELLOW, enabled = useColor)
                val value = parts[1].colorize(AnsiColor.GREEN, enabled = useColor)
                "$key=$value"
            } else {
                it
            }
        }
}
