package dev.mokksy.mokksy.utils.highlight

import org.fusesource.jansi.Ansi

private val colorSupported by lazy {
    runCatching { Ansi::class.java.getMethod("isDetected").invoke(null) as Boolean }
        .getOrDefault(false)
}

/**
 * Determines whether ANSI color output is supported in the current environment.
 *
 * @return `true` if ANSI color support is detected; otherwise, `false`.
 */
internal actual fun isColorSupported(): Boolean = colorSupported
