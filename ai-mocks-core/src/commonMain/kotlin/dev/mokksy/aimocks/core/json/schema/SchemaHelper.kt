package dev.mokksy.aimocks.core.json.schema

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Helper object for working with JSON schemas.
 *
 * Provides utility functions for parsing and validating JSON Schema definitions.
 */
public object SchemaHelper {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    /**
     * Parses a [JsonElement] into a [JsonSchemaDefinition].
     *
     * @param schemaJson The JSON element containing the schema
     * @return The parsed schema definition, or null if parsing fails
     */
    public fun parseSchema(schemaJson: JsonElement?): JsonSchemaDefinition? =
        schemaJson?.let {
            try {
                json.decodeFromJsonElement(JsonSchemaDefinition.serializer(), it)
            } catch (_: SerializationException) {
                null
            }
        }

    /**
     * Checks if a schema has a property with the specified name.
     *
     * @param schema The schema definition to check
     * @param propertyName The name of the property
     * @return true if the property exists, false otherwise
     */
    public fun hasProperty(
        schema: JsonSchemaDefinition,
        propertyName: String,
    ): Boolean = schema.properties.containsKey(propertyName)

    /**
     * Gets the types of a property in the schema.
     *
     * @param schema The schema definition
     * @param propertyName The name of the property
     * @return The list of types for the property, or null if not found or not a value property
     */
    public fun getPropertyType(
        schema: JsonSchemaDefinition,
        propertyName: String,
    ): List<String>? =
        schema.properties[propertyName]?.let { prop ->
            when (prop) {
                is ValuePropertyDefinition -> prop.type
                else -> null
            }
        }

    /**
     * Checks if a property is required in the schema.
     *
     * @param schema The schema definition
     * @param propertyName The name of the property
     * @return true if the property is required, false otherwise
     */
    public fun isPropertyRequired(
        schema: JsonSchemaDefinition,
        propertyName: String,
    ): Boolean = schema.required.contains(propertyName)

    /**
     * Checks if all specified properties are required in the schema.
     *
     * @param schema The schema definition
     * @param propertyNames The names of properties to check
     * @return true if all properties are required, false otherwise
     */
    public fun hasAllRequiredProperties(
        schema: JsonSchemaDefinition,
        vararg propertyNames: String,
    ): Boolean = propertyNames.all { it in schema.required }

    /**
     * Gets the property definition for a given property name.
     *
     * @param schema The schema definition
     * @param propertyName The name of the property
     * @return The property definition, or null if not found
     */
    public fun getProperty(
        schema: JsonSchemaDefinition,
        propertyName: String,
    ): PropertyDefinition? = schema.properties[propertyName]

    /**
     * Gets the description of a property in the schema.
     *
     * @param schema The schema definition
     * @param propertyName The name of the property
     * @return The description of the property, or null if not found
     */
    public fun getPropertyDescription(
        schema: JsonSchemaDefinition,
        propertyName: String,
    ): String? =
        schema.properties[propertyName]?.let { prop ->
            when (prop) {
                is ValuePropertyDefinition -> prop.description
                else -> null
            }
        }
}
