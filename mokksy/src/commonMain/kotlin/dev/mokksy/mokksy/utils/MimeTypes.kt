package dev.mokksy.mokksy.utils

import io.ktor.http.ContentType

/**
 * Represents the MIME type of content for use in operations involving media type definitions.
 *
 * This type alias simplifies the representation of MIME types, typically formatted as "type/subtype".
 * It is used in methods for encoding data URLs or converting instances like ContentType into string
 * representations of MIME types.
 */
public typealias MimeType = String

/**
 * Converts a [ContentType] instance to its corresponding MIME type string representation.
 *
 * @return A string representation of the MIME type in the format "type/subtype".
 */
public fun ContentType.asMimeType(): MimeType = "$contentType/$contentSubtype"
