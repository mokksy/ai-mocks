package dev.mokksy.aimocks.core.json.schema

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.schema.json.CommonSchemaAttributes
import kotlinx.schema.json.JsonSchema
import kotlinx.schema.json.PropertyDefinition
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test

internal class SchemaHelperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parseSchema should return null for null input`() {
        // when
        val result = SchemaHelper.parseSchema(null)

        // then
        result shouldBe null
    }

    @Test
    fun `parseSchema should parse valid JSON schema`() {
        // given
        val schemaJson = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("name") {
                    put("type", "string")
                }
            }
            put("required", kotlinx.serialization.json.buildJsonArray {
                add("name")
            })
        }

        // when
        val result = SchemaHelper.parseSchema(schemaJson)

        // then
        result shouldBe JsonSchema(
            type = listOf("object"),
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
            required = listOf("name"),
        )
    }

    @Test
    fun `parseSchema should return null for invalid JSON schema`() {
        // given
        val invalidJson = buildJsonObject {
            put("invalidField", "invalidValue")
        }

        // when
        val result = SchemaHelper.parseSchema(invalidJson)

        // then
        result shouldBe null
    }

    @Test
    fun `parseSchema should handle schema with multiple properties`() {
        // given
        val schemaJson = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("name") {
                    put("type", "string")
                }
                putJsonObject("age") {
                    put("type", "number")
                }
                putJsonObject("email") {
                    put("type", "string")
                }
            }
        }

        // when
        val result = SchemaHelper.parseSchema(schemaJson)

        // then
        assertSoftly(result) {
            this shouldBe JsonSchema(
                type = listOf("object"),
                properties = mapOf(
                    "name" to CommonSchemaAttributes(type = listOf("string")),
                    "age" to CommonSchemaAttributes(type = listOf("number")),
                    "email" to CommonSchemaAttributes(type = listOf("string")),
                ),
            )
        }
    }

    @Test
    fun `hasProperty should return true when property exists`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.hasProperty(schema, "name")

        // then
        result shouldBe true
    }

    @Test
    fun `hasProperty should return false when property does not exist`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.hasProperty(schema, "age")

        // then
        result shouldBe false
    }

    @Test
    fun `hasProperty should return false for empty schema`() {
        // given
        val schema = JsonSchema()

        // when
        val result = SchemaHelper.hasProperty(schema, "name")

        // then
        result shouldBe false
    }

    @Test
    fun `getPropertyType should return type for existing property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyType(schema, "name")

        // then
        result shouldBe listOf("string")
    }

    @Test
    fun `getPropertyType should return null for non-existing property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyType(schema, "age")

        // then
        result shouldBe null
    }

    @Test
    fun `getPropertyType should return multiple types for union types`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "value" to CommonSchemaAttributes(type = listOf("string", "number")),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyType(schema, "value")

        // then
        assertSoftly(result) {
            this shouldBe listOf("string", "number")
        }
    }

    @Test
    fun `getPropertyType should return null for non-CommonSchemaAttributes property`() {
        // given
        // Create a property definition that is not a CommonSchemaAttributes
        val schema = JsonSchema(
            properties = mapOf(
                "reference" to PropertyDefinition.Ref("\$ref" to "#/definitions/Something"),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyType(schema, "reference")

        // then
        result shouldBe null
    }

    @Test
    fun `isPropertyRequired should return true for required property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
            required = listOf("name"),
        )

        // when
        val result = SchemaHelper.isPropertyRequired(schema, "name")

        // then
        result shouldBe true
    }

    @Test
    fun `isPropertyRequired should return false for non-required property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
                "age" to CommonSchemaAttributes(type = listOf("number")),
            ),
            required = listOf("name"),
        )

        // when
        val result = SchemaHelper.isPropertyRequired(schema, "age")

        // then
        result shouldBe false
    }

    @Test
    fun `isPropertyRequired should return false when required list is empty`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.isPropertyRequired(schema, "name")

        // then
        result shouldBe false
    }

    @Test
    fun `hasAllRequiredProperties should return true when all properties are required`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
                "age" to CommonSchemaAttributes(type = listOf("number")),
            ),
            required = listOf("name", "age"),
        )

        // when
        val result = SchemaHelper.hasAllRequiredProperties(schema, "name", "age")

        // then
        result shouldBe true
    }

    @Test
    fun `hasAllRequiredProperties should return false when some properties are not required`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
                "age" to CommonSchemaAttributes(type = listOf("number")),
            ),
            required = listOf("name"),
        )

        // when
        val result = SchemaHelper.hasAllRequiredProperties(schema, "name", "age")

        // then
        result shouldBe false
    }

    @Test
    fun `hasAllRequiredProperties should return false when no properties are required`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
                "age" to CommonSchemaAttributes(type = listOf("number")),
            ),
        )

        // when
        val result = SchemaHelper.hasAllRequiredProperties(schema, "name", "age")

        // then
        result shouldBe false
    }

    @Test
    fun `hasAllRequiredProperties should return true when checking single required property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
            required = listOf("name"),
        )

        // when
        val result = SchemaHelper.hasAllRequiredProperties(schema, "name")

        // then
        result shouldBe true
    }

    @Test
    fun `hasAllRequiredProperties should return true when checking no properties`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
            required = listOf("name"),
        )

        // when
        val result = SchemaHelper.hasAllRequiredProperties(schema)

        // then
        result shouldBe true
    }

    @Test
    fun `getProperty should return property definition when property exists`() {
        // given
        val propertyDef = CommonSchemaAttributes(type = listOf("string"))
        val schema = JsonSchema(
            properties = mapOf(
                "name" to propertyDef,
            ),
        )

        // when
        val result = SchemaHelper.getProperty(schema, "name")

        // then
        result shouldBe propertyDef
    }

    @Test
    fun `getProperty should return null when property does not exist`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.getProperty(schema, "age")

        // then
        result shouldBe null
    }

    @Test
    fun `getProperty should return null for empty schema`() {
        // given
        val schema = JsonSchema()

        // when
        val result = SchemaHelper.getProperty(schema, "name")

        // then
        result shouldBe null
    }

    @Test
    fun `getPropertyDescription should return description when property has description`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(
                    type = listOf("string"),
                    description = "User's full name",
                ),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyDescription(schema, "name")

        // then
        result shouldBe "User's full name"
    }

    @Test
    fun `getPropertyDescription should return null when property has no description`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyDescription(schema, "name")

        // then
        result shouldBe null
    }

    @Test
    fun `getPropertyDescription should return null when property does not exist`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "name" to CommonSchemaAttributes(type = listOf("string")),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyDescription(schema, "age")

        // then
        result shouldBe null
    }

    @Test
    fun `getPropertyDescription should return null for non-CommonSchemaAttributes property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "reference" to PropertyDefinition.Ref("\$ref" to "#/definitions/Something"),
            ),
        )

        // when
        val result = SchemaHelper.getPropertyDescription(schema, "reference")

        // then
        result shouldBe null
    }

    @Test
    fun `should handle complex schema with multiple required properties and descriptions`() {
        // given
        val schema = JsonSchema(
            type = listOf("object"),
            properties = mapOf(
                "name" to CommonSchemaAttributes(
                    type = listOf("string"),
                    description = "User's full name",
                ),
                "age" to CommonSchemaAttributes(
                    type = listOf("number"),
                    description = "User's age in years",
                ),
                "email" to CommonSchemaAttributes(
                    type = listOf("string"),
                    description = "User's email address",
                ),
                "phone" to CommonSchemaAttributes(type = listOf("string")),
            ),
            required = listOf("name", "email"),
        )

        // then
        assertSoftly {
            SchemaHelper.hasProperty(schema, "name") shouldBe true
            SchemaHelper.hasProperty(schema, "age") shouldBe true
            SchemaHelper.hasProperty(schema, "email") shouldBe true
            SchemaHelper.hasProperty(schema, "phone") shouldBe true
            SchemaHelper.hasProperty(schema, "address") shouldBe false

            SchemaHelper.isPropertyRequired(schema, "name") shouldBe true
            SchemaHelper.isPropertyRequired(schema, "email") shouldBe true
            SchemaHelper.isPropertyRequired(schema, "age") shouldBe false
            SchemaHelper.isPropertyRequired(schema, "phone") shouldBe false

            SchemaHelper.hasAllRequiredProperties(schema, "name", "email") shouldBe true
            SchemaHelper.hasAllRequiredProperties(schema, "name", "age") shouldBe false

            SchemaHelper.getPropertyDescription(schema, "name") shouldBe "User's full name"
            SchemaHelper.getPropertyDescription(schema, "age") shouldBe "User's age in years"
            SchemaHelper.getPropertyDescription(schema, "email") shouldBe "User's email address"
            SchemaHelper.getPropertyDescription(schema, "phone") shouldBe null

            SchemaHelper.getPropertyType(schema, "name") shouldBe listOf("string")
            SchemaHelper.getPropertyType(schema, "age") shouldBe listOf("number")
        }
    }

    @Test
    fun `parseSchema should handle schema with empty properties`() {
        // given
        val schemaJson = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
            }
        }

        // when
        val result = SchemaHelper.parseSchema(schemaJson)

        // then
        result shouldBe JsonSchema(
            type = listOf("object"),
            properties = emptyMap(),
        )
    }

    @Test
    fun `should work with schema containing array type property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "tags" to CommonSchemaAttributes(type = listOf("array")),
            ),
        )

        // when & then
        assertSoftly {
            SchemaHelper.hasProperty(schema, "tags") shouldBe true
            SchemaHelper.getPropertyType(schema, "tags") shouldBe listOf("array")
        }
    }

    @Test
    fun `should work with schema containing boolean type property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "isActive" to CommonSchemaAttributes(type = listOf("boolean")),
            ),
        )

        // when & then
        assertSoftly {
            SchemaHelper.hasProperty(schema, "isActive") shouldBe true
            SchemaHelper.getPropertyType(schema, "isActive") shouldBe listOf("boolean")
        }
    }

    @Test
    fun `should work with schema containing null type property`() {
        // given
        val schema = JsonSchema(
            properties = mapOf(
                "optionalField" to CommonSchemaAttributes(type = listOf("string", "null")),
            ),
        )

        // when & then
        assertSoftly {
            SchemaHelper.hasProperty(schema, "optionalField") shouldBe true
            val types = SchemaHelper.getPropertyType(schema, "optionalField")
            types?.size shouldBe 2
            types shouldContain "string"
            types shouldContain "null"
        }
    }
}