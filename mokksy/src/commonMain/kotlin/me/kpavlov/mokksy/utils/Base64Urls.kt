package me.kpavlov.mokksy.utils

import io.ktor.http.ContentType
import io.ktor.http.defaultForFilePath
import java.net.URL
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Converts the byte array into a Base64 encoded data URL string.
 *
 * @param mimeType The MIME type to include in the data URL, specifying the media type of the data.
 * @return The Base64 encoded data URL string containing the MIME type and the encoded content.
 * @see [data: URLs](https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/data)
 */
@OptIn(ExperimentalEncodingApi::class)
public fun ByteArray.asBase64DataUrl(mimeType: MimeType): String {
    val base64 = Base64.UrlSafe.encode(this)
    return "data:$mimeType;base64,$base64"
}

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
