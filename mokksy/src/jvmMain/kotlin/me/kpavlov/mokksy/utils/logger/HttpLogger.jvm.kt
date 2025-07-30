package me.kpavlov.mokksy.utils.logger

import org.fusesource.jansi.Ansi

internal actual fun isColorSupported(): Boolean = Ansi.isDetected()
