package me.kpavlov.mokksy.utils.logger

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingRequest
import me.kpavlov.mokksy.utils.logger.Highlighting.highlightBody


public enum class ColorTheme { LIGHT_ON_DARK, DARK_ON_LIGHT }

internal expect fun isColorSupported(): Boolean

public enum class AnsiColor(public val code: String) {
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

internal fun colorize(text: String, color: AnsiColor, enabled: Boolean = true): String {
    return if (enabled) "${color.code}$text${AnsiColor.RESET.code}" else text
}

public open class HttpFormatter(
    theme: ColorTheme = ColorTheme.LIGHT_ON_DARK,
    protected val useColor: Boolean = isColorSupported(),
) {

    private fun method(method: HttpMethod): String {
        val color = when (method) {
            HttpMethod.Get -> AnsiColor.BLUE
            HttpMethod.Post -> AnsiColor.GREEN
            HttpMethod.Delete -> AnsiColor.RED
            else -> AnsiColor.STRONGER
        }
        return colorize(method.value, color, useColor)
    }

    protected val colors: ColorScheme = when (theme) {
        ColorTheme.LIGHT_ON_DARK -> ColorScheme(
            path = AnsiColor.STRONGER,
            headerName = AnsiColor.YELLOW,
            headerValue = AnsiColor.PALE,
            body = AnsiColor.LIGHT_GRAY
        )

        ColorTheme.DARK_ON_LIGHT -> ColorScheme(
            path = AnsiColor.STRONGER,
            headerName = AnsiColor.BLACK,
            headerValue = AnsiColor.PALE,
            body = AnsiColor.LIGHT_GRAY
        )
    }

    public fun requestLine(method: HttpMethod, path: String): String =
        "${method(method)} ${
            colorize(
                path,
                colors.path,
                useColor
            )
        }"

    public fun header(k: String, values: List<String>): String =
        "${colorize(k, colors.headerName, useColor)}: ${
            colorize(
                values.joinToString(separator = ",", prefix = "[", postfix = "]"),
                colors.headerValue,
                useColor
            )
        }"

    public fun formatBody(body: String?, contentType: ContentType = ContentType.Any): String {
        if (body.isNullOrBlank()) return ""
        return if (useColor) highlightBody(body, contentType) else body
    }

    internal suspend fun formatRequest(request: RoutingRequest): String {
        val body = request.call.receive(String::class)
        return """
        |${requestLine(request.httpMethod, request.uri)}
        |${request.headers.entries().joinToString("\n") { header(it.key, it.value) }}
        |
        |${formatBody(body, request.contentType())}
        """.trimMargin()
    }


    public data class ColorScheme(
        val path: AnsiColor,
        val headerName: AnsiColor,
        val headerValue: AnsiColor,
        val body: AnsiColor
    )
}





