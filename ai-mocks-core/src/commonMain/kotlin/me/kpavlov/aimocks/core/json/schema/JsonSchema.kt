package me.kpavlov.aimocks.core.json.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import me.kpavlov.aimocks.core.json.schema.serializers.PropertyDefinitionSerializer
import me.kpavlov.mokksy.serializers.StringOrListSerializer

/**
 * Represents a JSON Schema definition
 *
 * @property name The name of the schema.
 * @property strict Whether to enable strict schema adherence.
 * @property schema The actual JSON schema definition.
 */
@Serializable
public data class JsonSchema(
    val name: String,
    val strict: Boolean = false,
    val schema: SchemaDefinition,
    val description: String? = null,
)

/**
 * Represents a JSON Schema definition.
 *
 * @property type The JSON schema type (e.g., "object", "array", "string").
 * @property properties A map of property definitions.
 * @property required List of required property names.
 * @property additionalProperties Whether to allow additional properties in the object.
 * @property description Optional description of the schema.
 */
@Serializable
public data class SchemaDefinition(
    val type: String,
    val properties: Map<String, PropertyDefinition>,
    val required: List<String> = emptyList(),
    val additionalProperties: Boolean? = null,
    val description: String? = null,
    val items: PropertyDefinition? = null,
)

/**
 * Represents a property definition in a JSON Schema.
 *
 * This is a sealed interface that serves as the base for all property definition types.
 * Different property types (string, number, array, object, reference) have different implementations.
 *
 * @see <a href="https://json-schema.org/draft/2020-12/draft-bhutton-json-schema-validation-00">
 *     JSON Schema Validation: A Vocabulary for Structural Validation of JSON
 *     </a>
 */
@Serializable(with = PropertyDefinitionSerializer::class)
public sealed interface PropertyDefinition {
    /**
     * The data type of the property.
     */
    public val type: List<String>?

    /**
     * Optional description of the property.
     */
    public val description: String?

    /**
     * Whether the property can be null.
     */
    public val nullable: Boolean
}

/**
 * Represents a string property in a JSON Schema.
 */
@Serializable
public data class StringPropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = listOf("string"),
    override val description: String? = null,
    override val nullable: Boolean = false,
    val format: String? = null,
    val enum: List<String>? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val default: JsonElement? = null,
    @SerialName("const")
    val constValue: JsonElement? = null,
) : PropertyDefinition

/**
 * Represents a numeric property (integer or number) in a JSON Schema.
 */
@Serializable
public data class NumericPropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = null,
    override val description: String? = null,
    override val nullable: Boolean = false,
    val multipleOf: Double? = null,
    val minimum: Double? = null,
    val exclusiveMinimum: Double? = null,
    val maximum: Double? = null,
    val exclusiveMaximum: Double? = null,
    val default: JsonElement? = null,
    @SerialName("const")
    val constValue: JsonElement? = null,
) : PropertyDefinition

/**
 * Represents an array property in a JSON Schema.
 */
@Serializable
public data class ArrayPropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = listOf("array"),
    override val description: String? = null,
    override val nullable: Boolean = false,
    val items: PropertyDefinition? = null,
    val minItems: UInt? = null,
    val maxItems: UInt? = null,
    val default: JsonElement? = null,
) : PropertyDefinition

/**
 * Represents an object property in a JSON Schema.
 */
@Serializable
public data class ObjectPropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = listOf("object"),
    override val description: String? = null,
    override val nullable: Boolean = false,
    val properties: Map<String, PropertyDefinition>? = null,
    val required: List<String>? = null,
    @SerialName("additionalProperties")
    val additionalProperties: Boolean? = null,
    val default: JsonElement? = null,
) : PropertyDefinition

/**
 * Represents a reference to another schema definition.
 */
@Serializable
public data class ReferencePropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = null,
    override val description: String? = null,
    override val nullable: Boolean = false,
    @SerialName("\$ref")
    val ref: String,
) : PropertyDefinition

/**
 * Represents a boolean property in a JSON Schema.
 */
@Serializable
public data class BooleanPropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    override val type: List<String>? = listOf("boolean"),
    override val description: String? = null,
    override val nullable: Boolean = false,
    val default: JsonElement? = null,
    @SerialName("const")
    val constValue: JsonElement? = null,
) : PropertyDefinition
