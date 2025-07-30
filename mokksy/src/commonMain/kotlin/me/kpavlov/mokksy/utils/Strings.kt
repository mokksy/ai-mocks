package me.kpavlov.mokksy.utils

public fun String?.ellipsizeMiddle(maxLength: Int): String? {
    if (this == null || this.length <= maxLength || maxLength < 5) return this

    val half = (maxLength - 3) / 2
    val start = this.take(half + (maxLength - 3) % 2)  // Adjust for odd maxLength
    val end = this.takeLast(half)
    return "$start...$end"
}
