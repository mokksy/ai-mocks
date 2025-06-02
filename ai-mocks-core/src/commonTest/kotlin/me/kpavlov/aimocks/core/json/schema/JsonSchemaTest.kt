package me.kpavlov.aimocks.core.json.schema

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class JsonSchemaTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = false
            prettyPrint = true
        }

    @Test
    fun `Should deserialize JsonSchema`() {
        val json =
            """
            {
              "name" : "Person",
              "strict" : false,
              "schema" : {
                "type" : "object",
                "properties" : {
                  "name" : {
                    "type" : "string",
                    "description" : "Person's name"
                  },
                  "age" : {
                    "type" : "integer",
                    "description" : "Person's age"
                  },
                  "weight" : {
                    "type" : "number",
                    "description" : "Weight in kilograms"
                  },
                  "height" : {
                    "type" : "number",
                    "description" : "Height in meters"
                  },
                  "married" : {
                    "type" : "boolean"
                  }
                },
                "required" : [ ]
              }
            }
            """.trimIndent()

        val schema = jsonParser.decodeFromString<JsonSchema>(json)

        schema.name shouldBe "Person"
        schema.strict shouldBe false
        schema.schema.shouldNotBeNull()
        schema.schema.type shouldBe "object"
        schema.schema.properties.shouldNotBeNull()
        schema.schema.properties.shouldHaveSize(5)

        // Verify specific properties
        val nameProperty = schema.schema.properties["name"]
        nameProperty.shouldNotBeNull()
        nameProperty.type shouldBe "string"
        nameProperty.description shouldBe "Person's name"

        val ageProperty = schema.schema.properties["age"]
        ageProperty.shouldNotBeNull()
        ageProperty.type shouldBe "integer"
        ageProperty.description shouldBe "Person's age"

        val marriedProperty = schema.schema.properties["married"]
        marriedProperty.shouldNotBeNull()
        marriedProperty.type shouldBe "boolean"
        marriedProperty.description.shouldBeNull()
    }

    @Test
    fun `Should deserialize ResponseFormat with JsonSchema`() {
        val json =
            """
            {
                "name" : "Person",
                "strict" : false,
                "schema" : {
                  "type" : "object",
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "description" : "Person's name"
                    },
                    "age" : {
                      "type" : "integer",
                      "description" : "Person's age"
                    },
                    "married" : {
                      "type" : "boolean"
                    }
                  },
                  "required" : [ ]
                }
            }
            """.trimIndent()

        val jsonSchema = jsonParser.decodeFromString<JsonSchema>(json)

        jsonSchema.shouldNotBeNull {
            name shouldBe "Person"
            schema.properties.shouldHaveSize(3)
            schema.properties["name"]?.type shouldBe "string"
            schema.properties["age"]?.type shouldBe "integer"
            schema.properties["married"]?.type shouldBe "boolean"
        }

    }

    @Test
    @Suppress("LongMethod")
    fun `Should deserialize complex JsonSchema`() {
        val json =
            """
            {
              "name": "ComplexSchema",
              "strict": true,
              "description": "A complex schema with various field types",
              "schema": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "string",
                    "format": "uuid",
                    "description": "Unique identifier"
                  },
                  "email": {
                    "type": "string",
                    "format": "email",
                    "pattern": "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$",
                    "description": "Email address"
                  },
                  "age": {
                    "type": "integer",
                    "minimum": 18,
                    "maximum": 100,
                    "description": "Age between 18 and 100"
                  },
                  "score": {
                    "type": "number",
                    "minimum": 0,
                    "maximum": 10,
                    "default": 5
                  },
                  "status": {
                    "type": "string",
                    "enum": ["active", "inactive", "pending"],
                    "description": "Current status"
                  },
                  "tags": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    },
                    "description": "List of tags"
                  },
                  "metadata": {
                    "type": "object",
                    "properties": {
                      "createdAt": {
                        "type": "string",
                        "format": "date-time"
                      },
                      "updatedAt": {
                        "type": "string",
                        "format": "date-time"
                      }
                    },
                    "required": ["createdAt"]
                  },
                  "nullable_field": {
                    "type": "string",
                    "nullable": true
                  }
                },
                "required": ["id", "email", "status"],
                "additionalProperties": false
              }
            }
            """.trimIndent()

        val schema = jsonParser.decodeFromString<JsonSchema>(json)

        // Basic validation
        schema.name shouldBe "ComplexSchema"
        schema.strict shouldBe true
        schema.description shouldBe "A complex schema with various field types"

        // Schema validation
        val schemaDefinition = schema.schema
        schemaDefinition.type shouldBe "object"
        schemaDefinition.additionalProperties shouldBe false
        schemaDefinition.required shouldHaveSize 3
        schemaDefinition.required shouldBe listOf("id", "email", "status")

        // Properties validation
        val properties = schemaDefinition.properties
//        properties shouldHaveSize 8

        // String with format
        val idProperty = properties["id"]!!
        idProperty.type shouldBe "string"
        idProperty.format shouldBe "uuid"

        // String with pattern
        val emailProperty = properties["email"]!!
        emailProperty.type shouldBe "string"
        emailProperty.format shouldBe "email"
        emailProperty.pattern shouldBe "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"

        // Numeric with min/max
        val ageProperty = properties["age"]!!
        ageProperty.type shouldBe "integer"
        ageProperty.minimum shouldBe 18.0
        ageProperty.maximum shouldBe 100.0

        // Enum field
        val statusProperty = properties["status"]!!
        statusProperty.type shouldBe "string"
        statusProperty.enum shouldBe listOf("active", "inactive", "pending")

        // Array field
        val tagsProperty = properties["tags"]!!
        tagsProperty.type shouldBe "array"
        tagsProperty.items.shouldNotBeNull()
        tagsProperty.items.type shouldBe "string"

        // Object field with nested properties
        val metadataProperty = properties["metadata"]!!
        metadataProperty.type shouldBe "object"
        metadataProperty.properties.shouldNotBeNull()
        metadataProperty.properties shouldHaveSize 2
        metadataProperty.required shouldBe listOf("createdAt")

        // Nullable field
        val nullableField = properties["nullable_field"]!!
        nullableField.type shouldBe "string"
        nullableField.nullable shouldBe true
    }
}
