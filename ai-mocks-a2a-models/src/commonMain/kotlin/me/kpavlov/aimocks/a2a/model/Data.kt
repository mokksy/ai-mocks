package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.MapOrPrimitiveSerializer

@JvmInline
@Serializable
public value class Data(
    private val value: Map<String, @Serializable(MapOrPrimitiveSerializer::class) Any>
) {

    public operator fun get(key: String): Any? = value[key]

    public companion object {
        private val EMPTY: Data = Data(emptyMap())
        public fun empty(): Data = EMPTY
        public fun of(vararg pairs: Pair<String, Any>): Data = Data(mapOf(*pairs))
    }
}
