package me.kpavlov.aimocks.a2a.model

import java.util.function.Consumer

/**
 * Builder for creating [DataPart] instances with a fluent DSL.
 *
 * Example usage:
 * ```kotlin
 * val dataPart = DataPartBuilder().apply {
 *     data = mapOf("key" to "value", "nested" to mapOf("inner" to 42))
 * }.create()
 * ```
 */
public class DataPartBuilder {
    public var data: MutableMap<String, Any> = mutableMapOf()
    public var metadata: Metadata? = null

    /**
     * Adds a key-value pair to the data map.
     *
     * @param key The key for the data entry
     * @param value The value for the data entry
     * @return This builder for chaining
     */
    public fun put(
        key: String,
        value: Any,
    ): DataPartBuilder =
        apply {
            data[key] = value
        }

    /**
     * Adds all entries from the provided map to the data map.
     *
     * @param map The map of entries to add
     * @return This builder for chaining
     */
    public fun putAll(map: Map<String, Any>): DataPartBuilder =
        apply {
            data.putAll(map)
        }

    /**
     * Sets the metadata for the data part.
     *
     * @param metadata The metadata to set
     * @return This builder for chaining
     */
    public fun metadata(metadata: Metadata): DataPartBuilder =
        apply {
            this.metadata = metadata
        }

    /**
     * Builds a [DataPart] instance with the configured properties.
     *
     * @param validate Whether to validate required properties
     * @return A new [DataPart] instance
     * @throws IllegalStateException if validation fails
     */
    public fun build(validate: Boolean = false): DataPart {
        if (validate) {
            require(data.isNotEmpty()) { "Data map must not be empty for DataPart" }
        }

        return DataPart(
            data = Data.of(data),
            metadata = metadata,
        )
    }
}

/**
 * Creates a new DataPart using the DSL builder.
 *
 * Example:
 * ```kotlin
 * val dataPart = dataPart {
 *     put("name", "John")
 *     put("age", 30)
 *     put("address", mapOf("city" to "New York", "zip" to "10001"))
 * }
 * ```
 *
 * @param init The lambda to configure the data part
 * @return A new DataPart instance
 */
public inline fun dataPart(init: DataPartBuilder.() -> Unit): DataPart =
    DataPartBuilder().apply(init).build()

/**
 * Creates a new DataPart using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the data part
 * @return A new DataPart instance
 */
public fun dataPart(init: Consumer<DataPartBuilder>): DataPart {
    val builder = DataPartBuilder()
    init.accept(builder)
    return builder.build()
}

/**
 * Creates a new DataPart using the DSL builder.
 *
 * @param init The lambda to configure the data part
 * @return A new DataPart instance
 */
public fun DataPart.Companion.create(init: DataPartBuilder.() -> Unit): DataPart =
    DataPartBuilder().apply(init).build()

/**
 * Creates a new DataPart using the Java-friendly Consumer.
 *
 * @param init The consumer to configure the data part
 * @return A new DataPart instance
 */
public fun DataPart.Companion.create(init: Consumer<DataPartBuilder>): DataPart {
    val builder = DataPartBuilder()
    init.accept(builder)
    return builder.build()
}
