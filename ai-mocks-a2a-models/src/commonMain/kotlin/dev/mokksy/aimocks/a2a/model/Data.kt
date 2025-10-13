package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.MapOrPrimitiveSerializer
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
public value class Data(
    private val value: Map<
        String,
        @Serializable(MapOrPrimitiveSerializer::class)
        Any,
    >,
) {
    public operator fun get(key: String): Any? = value[key]

    public fun asMap(): Map<String, Any> = value

    public companion object {
        private val EMPTY: Data = Data(emptyMap())

        @JvmStatic
        public fun empty(): Data = EMPTY

        @JvmStatic
        public fun of(vararg pairs: Pair<String, Any>): Data = Data(mapOf(*pairs))

        @JvmStatic
        public fun of(map: Map<String, Any>): Data = Data(map)
    }
}
