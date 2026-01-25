package dev.mokksy.mokksy.utils

import io.ktor.http.ContentType
import io.ktor.http.defaultForFile
import io.ktor.http.defaultForFilePath
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

@JvmOverloads
public fun File.asBase64DataUrl(
    mimeType: MimeType = ContentType.defaultForFile(this).toString(),
): String = this.readBytes().asBase64DataUrl(mimeType)

@JvmOverloads
public fun Path.asBase64DataUrl(
    mimeType: MimeType =
        ContentType.Companion
            .defaultForFilePath(
                fileName.toString(),
            ).asMimeType(),
): String = Files.readAllBytes(this).asBase64DataUrl(mimeType)

/**
 * Converts the content of the URL into a Base64 encoded data URL string.
 *
 * @param mimeType The MIME type to include in the data URL, specifying the media type of the URL's content.
 * @return The Base64 encoded data URL string containing the MIME type and the encoded content retrieved from the URL.
 * @see [data: URLs](https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/data)
 */
@JvmOverloads
public fun URL.asBase64DataUrl(
    mimeType: MimeType = ContentType.defaultForFilePath(this.path).asMimeType(),
): String = readBytes().asBase64DataUrl(mimeType)
