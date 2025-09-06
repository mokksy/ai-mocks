package me.kpavlov.mokksy.utils

/**
 * Truncates the string to the specified maximum length by replacing the middle portion with an ellipsis.
 *
 * If the string is `null`, its length is less than or equal to `maxLength`, or `maxLength` is less than 5, the original string is returned unchanged. Otherwise, the string is shortened by keeping the start and end segments and inserting "..." in the middle so that the total length does not exceed `maxLength`.
 *
 * @param maxLength The maximum allowed length of the resulting string, including the ellipsis. Must be at least 5 to perform truncation.
 * @return The original string if no truncation is needed, or a new string with the middle replaced by an ellipsis if truncation occurs.
 */
public fun String?.ellipsizeMiddle(maxLength: Int): String? {
    if (this == null || this.length <= maxLength || maxLength < 5) return this

    val half = (maxLength - 3) / 2
    val start = this.take(half + (maxLength - 3) % 2) // Adjust for odd maxLength
    val end = this.takeLast(half)
    return "$start...$end"
}
