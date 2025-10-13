package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.MapOrPrimitiveSerializer
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
public value class Metadata(
    private val value: Map<
        String,
        @Serializable(MapOrPrimitiveSerializer::class)
        Any,
    >,
) {
    public operator fun get(key: String): Any? = value[key]

    public companion object {
        private val EMPTY: Metadata = Metadata(emptyMap())

        @JvmStatic
        public fun empty(): Metadata = EMPTY

        @JvmStatic
        public fun of(vararg pairs: Pair<String, Any>): Metadata = Metadata(mapOf(*pairs))
    }
}
