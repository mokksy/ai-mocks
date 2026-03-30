package dev.mokksy.aimocks.core.json.schema

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SchemaHelperTest {
    private val schemaJson =
        Json.parseToJsonElement(
            """
            {
              "type": "object",
              "properties": {
                "location": {
                  "type": "string",
                  "description": "The city and state, e.g. San Francisco, CA"
                },
                "unit": {
                  "type": "string"
                }
              },
              "required": ["location"]
            }
            """.trimIndent(),
        )

    @Test
    fun `parseSchema returns null for null input`() {
        SchemaHelper.parseSchema(null).shouldBeNull()
    }

    @Test
    fun `verify valid schema`() {
        SchemaHelper.parseSchema(schemaJson) shouldNotBeNull {
            SchemaHelper.hasProperty(this, "location") shouldBe true
            SchemaHelper.getPropertyDescription(this, "location") shouldBe
                "The city and state, e.g. San Francisco, CA"
            SchemaHelper.getPropertyType(this, "location") shouldBe listOf("string")
            SchemaHelper.getPropertyDescription(this, "unit").shouldBeNull()

            SchemaHelper.isPropertyRequired(this, "location") shouldBe true
            SchemaHelper.isPropertyRequired(this, "unit") shouldBe false
            SchemaHelper.hasAllRequiredProperties(this, "location") shouldBe true
            SchemaHelper.hasAllRequiredProperties(this, "location", "unit") shouldBe false

            SchemaHelper.hasProperty(this, "nonexistent") shouldBe false
            SchemaHelper.getPropertyType(this, "nonexistent").shouldBeNull()
            SchemaHelper.getPropertyDescription(this, "nonexistent").shouldBeNull()
        }
    }
}
