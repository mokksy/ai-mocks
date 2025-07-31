package me.kpavlov.mokksy.utils.logger

import org.fusesource.jansi.Ansi

/**
 * Determines whether ANSI color output is supported in the current environment.
 *
 * @return `true` if ANSI color support is detected; otherwise, `false`.
 */
internal actual fun isColorSupported(): Boolean = Ansi.isDetected()
