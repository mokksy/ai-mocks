package me.kpavlov.mokksy.utils.logger

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.routing.RoutingRequest
import me.kpavlov.mokksy.utils.logger.Highlighting.highlightBody

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
 * Wraps the given text with the specified ANSI color code if coloring is enabled.
 *
 * @param text The text to colorize.
 * @param color The ANSI color to apply.
 * @param enabled Whether to apply colorization; if false, returns the original text.
 * @return The colorized text if enabled, otherwise the original text.
 */
internal fun colorize(
    text: String,
    color: AnsiColor,
    enabled: Boolean = true,
): String = if (enabled) "${color.code}$text${AnsiColor.RESET.code}" else text

public open class HttpFormatter(
    theme: ColorTheme = ColorTheme.LIGHT_ON_DARK,
    protected val useColor: Boolean = isColorSupported(),
) {
    /**
     * Returns the HTTP method name colorized according to its type and the current color settings.
     *
     * GET, POST, and DELETE methods are assigned specific colors; other methods are rendered in bold.
     */
    private fun method(method: HttpMethod): String {
        val color =
            when (method) {
                HttpMethod.Get -> AnsiColor.BLUE
                HttpMethod.Post -> AnsiColor.GREEN
                HttpMethod.Delete -> AnsiColor.RED
                else -> AnsiColor.STRONGER
            }
        return colorize(method.value, color, useColor)
    }

    protected val colors: ColorScheme =
        when (theme) {
            ColorTheme.LIGHT_ON_DARK ->
                ColorScheme(
                    path = AnsiColor.STRONGER,
                    headerName = AnsiColor.YELLOW,
                    headerValue = AnsiColor.PALE,
                    body = AnsiColor.LIGHT_GRAY,
                )

            ColorTheme.DARK_ON_LIGHT ->
                ColorScheme(
                    path = AnsiColor.STRONGER,
                    headerName = AnsiColor.BLACK,
                    headerValue = AnsiColor.PALE,
                    body = AnsiColor.LIGHT_GRAY,
                )
        }

    /**
     * Formats an HTTP request line with the method and path, applying colorization based on the selected theme.
     *
     * @param method The HTTP method to display.
     * @param path The request path to display.
     * @return The formatted and optionally colorized request line.
     */
    public fun requestLine(
        method: HttpMethod,
        path: String,
    ): String =
        "${method(method)} ${
            colorize(
                path,
                colors.path,
                useColor,
            )
        }"

    public fun responseLine(
        httpVersion: String,
        status: HttpStatusCode,
    ): String =
        colorize(
            "$httpVersion ${status.value} ${status.description}",
            AnsiColor.STRONGER,
            useColor,
        )

    /**
     * Formats an HTTP header line with colorized header name and values.
     *
     * @param k The header name.
     * @param values The list of header values.
     * @return The formatted and colorized header line as a string.
     */
    public fun header(
        k: String,
        values: List<String>,
    ): String =
        "${colorize(k, colors.headerName, useColor)}: ${
            colorize(
                values.joinToString(separator = ",", prefix = "[", postfix = "]"),
                colors.headerValue,
                useColor,
            )
        }"

    /**
     * Formats the HTTP request body, applying syntax highlighting if color output is enabled.
     *
     * Returns an empty string if the body is null or blank.
     * If color output is enabled, the body is highlighted according to its content type;
     * otherwise, the raw body string is returned.
     *
     * @param body The HTTP request body to format.
     * @param contentType The content type of the body, used for syntax highlighting.
     * @return The formatted body string, or an empty string if the body is null or blank.
     */
    public fun formatBody(
        body: String?,
        contentType: ContentType = ContentType.Any,
    ): String {
        if (body.isNullOrBlank()) return ""
        return if (useColor) highlightBody(body, contentType) else body
    }

    /**
     * Formats an HTTP request into a colorized, multi-line string representation.
     *
     * The output includes the request line, all headers, and the request body,
     * with color highlighting applied according to the formatter's theme and color settings.
     *
     * @param request The HTTP routing request to format.
     * @return A formatted string representing the full HTTP request.
     */
    internal suspend fun formatRequest(request: RoutingRequest): String {
        val body = request.call.receiveText()
        return buildString {
            appendLine(requestLine(request.httpMethod, request.uri))
            request.headers.entries().forEach { (key, value) ->
                appendLine(header(key, value))
            }
            appendLine()
            appendLine(formatBody(body, request.contentType()))
        }
    }

    internal fun formatResponseHeader(
        httpVersion: String,
        status: HttpStatusCode,
        headers: ResponseHeaders,
    ): String =
        buildString {
            appendLine(responseLine(httpVersion, status))
            headers.allValues().entries().forEach { (key, value) ->
                appendLine(header(key, value))
            }
        }

    internal fun formatResponse(
        httpVersion: String,
        status: HttpStatusCode,
        headers: ResponseHeaders,
        body: String?,
        contentType: ContentType,
    ): String =
        buildString {
            append(formatResponseHeader(httpVersion, status, headers))
            appendLine()
            appendLine(formatBody(body = body, contentType = contentType))
        }

    internal fun formatResponseChunk(
        chunk: String?,
        contentType: ContentType = ContentType.Text.Plain,
    ): String {
        if (chunk.isNullOrBlank()) return ""
        return if (useColor) highlightBody(chunk, contentType) else chunk
    }

    public data class ColorScheme(
        val path: AnsiColor,
        val headerName: AnsiColor,
        val headerValue: AnsiColor,
        val body: AnsiColor,
    )
}
