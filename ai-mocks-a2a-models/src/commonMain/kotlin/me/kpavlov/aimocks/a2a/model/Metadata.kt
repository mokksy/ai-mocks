package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.MapOrPrimitiveSerializer

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

        public fun empty(): Metadata = EMPTY

        public fun of(vararg pairs: Pair<String, Any>): Metadata = Metadata(mapOf(*pairs))
    }
}
