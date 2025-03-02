package me.kpavlov.mokksy

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import kotlinx.serialization.SerializationException

/**
 * A [ContentConverter] implementation that delegates to a primary converter (KotlinX Serialization)
 * and falls back to a secondary converter when the primary one fails.
 *
 * This class is used to handle content serialization and deserialization
 * in scenarios where different serialization strategies might be necessary based on the data.
 *
 * @constructor
 * @param kotlinxSerializationConverter The primary KotlinX Serialization-based content converter.
 * @param fallbackConverter The secondary fallback content converter used
 * when the primary converter fails to serialize or deserialize the content.
 */
internal class KotlinxFirstContentConverter(
    private val kotlinxSerializationConverter: KotlinxSerializationConverter,
    private val fallbackConverter: ContentConverter,
) : ContentConverter {
    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?,
    ): OutgoingContent? =
        try {
            kotlinxSerializationConverter.serialize(contentType, charset, typeInfo, value)
        } catch (_: SerializationException) {
            fallbackConverter.serialize(contentType, charset, typeInfo, value)
        }

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel,
    ): Any? =
        try {
            kotlinxSerializationConverter.deserialize(charset, typeInfo, content)
        } catch (_: Exception) {
            fallbackConverter.deserialize(charset, typeInfo, content)
        }
}
