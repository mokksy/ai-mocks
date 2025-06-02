package me.kpavlov.aimocks.core.json.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
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
    val description: String? = null
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
    val items: PropertyDefinition? = null
)

/**
 * Represents a property definition in a JSON Schema.
 *
 * @property type The data type of the property.
 * @property description Optional description of the property.
 * @property format Optional format of the property (e.g., "date-time", "email").
 * @property enum Optional list of allowed values for the property.
 * @property minimum Optional minimum value for numeric properties.
 * @property maximum Optional maximum value for numeric properties.
 * @property minLength Optional minimum length for string properties.
 * @property maxLength Optional maximum length for string properties.
 * @property pattern Optional regex pattern for string properties.
 * @property nullable Whether the property can be null.
 * @property default Optional default value for the property.
 * @property items Optional schema for array items.
 * @property properties Optional nested properties for object properties.
 * @property ref Optional reference to another schema definition.
 *
 * @see <a href="https://json-schema.org/draft/2020-12/draft-bhutton-json-schema-validation-00">
 *     JSON Schema Validation: A Vocabulary for Structural Validation of JSON
 *     </a>
 */
@Serializable
public data class PropertyDefinition(
    @Serializable(with = StringOrListSerializer::class)
    val type: List<String>,
    val description: String? = null,
    val format: String? = null,
    val enum: List<String>? = null,
    val minimum: Double? = null,
    val maximum: Double? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val nullable: Boolean = false,
    val default: JsonElement? = null,
    val items: PropertyDefinition? = null,
    val properties: Map<String, PropertyDefinition>? = null,
    val required: List<String>? = null,
    @SerialName("\$ref")
    val ref: String? = null,
    @SerialName("const")
    val constValue: JsonElement? = null,
    @SerialName("additionalProperties")
    val additionalProperties: Boolean? = null
)
