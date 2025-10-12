package me.kpavlov.mokksy.utils.highlight

import io.ktor.http.ContentType

public object Highlighting {
    /**
     * Applies ANSI color highlighting to an HTTP body string based on its content type.
     *
     * @param body The HTTP body content to highlight.
     * @param contentType The content type of the body.
     * @return The highlighted body string with ANSI color codes.
     */
    public fun highlightBody(
        body: String,
        contentType: ContentType,
        useColor: Boolean = isColorSupported(),
    ): String =
        when {
            contentType.match(ContentType.Application.Json) ||
                contentType.contentSubtype.endsWith("+json", ignoreCase = true) ->
                JsonHighlighter.highlight(
                    json = body,
                    useColor = useColor,
                )

            contentType.match(ContentType.Application.FormUrlEncoded) ->
                FormParamsHighlighter.highlight(
                    data = body,
                    useColor = useColor,
                )

            else -> body.colorize(AnsiColor.LIGHT_GRAY, enabled = useColor)
        }
}

public enum class ColorTheme { LIGHT_ON_DARK, DARK_ON_LIGHT }

/**
 * Determines whether ANSI color output is supported on the current platform.
 *
 * @return `true` if ANSI color codes can be used for output; otherwise, `false`.
 */
internal expect fun isColorSupported(): Boolean

public enum class AnsiColor(
    public val code: String,
) {
    RESET("\u001B[0m"),
    STRONGER("\u001B[1m"),
    PALE("\u001B[2m"),
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    MAGENTA("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m"),
    LIGHT_GRAY("\u001B[37m"),
    LIGHT_GRAY_BOLD("\u001B[37;1m"),
    DARK_GRAY("\u001B[90m"),
}

/**
 * Wraps the String with the specified ANSI color code if coloring is enabled.
 *
 * @param color The ANSI color to apply.
 * @param enabled Whether to apply colorization; if false, returns the original text.
 * @return The colorized text if enabled, otherwise the original text.
 */
internal fun String.colorize(
    color: AnsiColor,
    enabled: Boolean = isColorSupported(),
): String = if (enabled) "${color.code}$this${AnsiColor.RESET.code}" else this
