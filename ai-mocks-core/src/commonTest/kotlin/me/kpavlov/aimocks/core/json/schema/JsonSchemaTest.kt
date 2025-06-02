package me.kpavlov.aimocks.core.json.schema

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
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
    fun `Should deserialize simple JsonSchema`() {
        // language=json
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
                    "type" : ["number", "null"],
                    "description" : "Weight in kilograms"
                  },
                  "height" : {
                    "type" : "number",
                    "description" : "Height in meters"
                  },
                  "married" : {
                    "type" : ["boolean", "null"]
                  }
                },
                "required" : [ "name", "age", "weight", "height", "married"]
              }
            }
            """.trimIndent()

        val schema = jsonParser.decodeFromString<JsonSchema>(json)

        schema.name shouldBe "Person"
        schema.strict shouldBe false
        schema.schema shouldNotBeNull {
            this.type shouldBe "object"
            this.required shouldBeEqual listOf("name", "age", "weight", "height", "married")
            this.properties shouldNotBeNull {
                shouldHaveSize(5)
                this["name"] shouldNotBeNull {
                    type shouldBe listOf("string")
                }
                this["age"] shouldNotBeNull {
                    type shouldBe listOf("integer")
                }
                this["weight"] shouldNotBeNull {
                    type shouldBe listOf("number", "null")
                }
                this["height"] shouldNotBeNull {
                    type shouldBe listOf("number")
                }
                this["married"] shouldNotBeNull {
                    type shouldBe listOf("boolean", "null")
                }
            }
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
                    "description": "Metadata about the user",
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
                  "steps": {
                    "type": "array",
                    "description": "Steps taken by the user",
                    "items": {
                      "type": "object",
                      "properties": {
                        "explanation": { "type": "string" },
                        "output": { "type": "string" }
                      },
                      "required": ["explanation", "output"],
                      "additionalProperties": false
                    }
                  },
                  "nullable_field": {
                    "description": "Nullable field",
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
        properties shouldHaveSize 9

        // String with format
        properties["id"] shouldNotBeNull {
            type shouldBe listOf("string")
            nullable shouldBe false
            format shouldBe "uuid"
            description shouldBe "Unique identifier"
        }

        // String with pattern
        properties["email"] shouldNotBeNull {
            type shouldBe listOf("string")
            nullable shouldBe false
            format shouldBe "email"
            pattern shouldBe "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+\$"
            description shouldBe "Email address"
        }

        // Numeric with min/max
        properties["age"] shouldNotBeNull {
            type shouldBe listOf("integer")
            nullable shouldBe false
            minimum shouldBe 18.0
            maximum shouldBe 100.0
            description shouldBe "Age between 18 and 100"
        }

        // Enum field
        properties["status"] shouldNotBeNull {
            description shouldBe "Current status"
            enum shouldBe listOf("active", "inactive", "pending")
            nullable shouldBe false
        }

        // Array field
        properties["tags"] shouldNotBeNull {
            description shouldBe "List of tags"
            nullable shouldBe false
            items.shouldNotBeNull()
            items.type shouldBe listOf("string")
            items.nullable shouldBe false
            items.description.shouldBeNull()
            items.enum.shouldBeNull()
        }

        // Object field with nested properties
        properties["metadata"] shouldNotBeNull {
            description shouldBe "Metadata about the user"
            nullable shouldBe false
            this.properties.shouldNotBeNull()
            this.properties shouldHaveSize 2
        }

        // Nullable field
        properties["nullable_field"] shouldNotBeNull {
            description shouldBe "Nullable field"
            nullable shouldBe true
            enum.shouldBeNull()
            items.shouldBeNull()
            this.properties.shouldBeNull()
            format.shouldBeNull()
            pattern.shouldBeNull()
            minimum.shouldBeNull()
            maximum.shouldBeNull()
        }

        // Array of objects
        properties["steps"] shouldNotBeNull {
            description shouldBe "Steps taken by the user"
            nullable shouldBe false
            items shouldNotBeNull {
                type shouldBe listOf("object")
                nullable shouldBe false
                description.shouldBeNull()
                enum.shouldBeNull()
                format.shouldBeNull()
                pattern.shouldBeNull()
                minimum.shouldBeNull()
                maximum.shouldBeNull()
                this.properties shouldNotBeNull {
                    shouldHaveSize(2)
                    this["explanation"] shouldNotBeNull {
                        type shouldBe listOf("string")
                    }
                    this["output"] shouldNotBeNull {
                        type shouldBe listOf("string")
                    }
                    additionalProperties shouldBe false
                }
            }
        }
    }
}
