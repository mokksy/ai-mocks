package me.kpavlov.aimocks.core.json.schema

import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

internal class AdvancedJsonSchemaTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = false
            prettyPrint = true
        }

    @Test
    @Suppress("LongMethod")
    fun `Should deserialize schema with array of objects`() {
        val json =
            """
            {
              "name": "ProductCatalog",
              "strict": true,
              "schema": {
                "type": "object",
                "properties": {
                  "products": {
                    "type": "array",
                    "description": "List of products",
                    "items": {
                      "type": "object",
                      "properties": {
                        "id": {
                          "type": "string"
                        },
                        "name": {
                          "type": "string"
                        },
                        "price": {
                          "type": "number",
                          "minimum": 0
                        },
                        "categories": {
                          "type": "array",
                          "items": {
                            "type": "string"
                          }
                        }
                      },
                      "required": ["id", "name", "price"]
                    }
                  },
                  "pagination": {
                    "type": "object",
                    "properties": {
                      "total": {
                        "type": "integer"
                      },
                      "page": {
                        "type": "integer",
                        "minimum": 1
                      },
                      "pageSize": {
                        "type": "integer",
                        "minimum": 1,
                        "maximum": 100
                      }
                    }
                  }
                },
                "required": ["products"]
              }
            }
            """.trimIndent()

        val schema = jsonParser.decodeFromString<JsonSchema>(json)

        schema.name shouldBe "ProductCatalog"
        schema.strict shouldBe true

        val schemaDefinition = schema.schema
        schemaDefinition.type shouldBe "object"
        schemaDefinition.required shouldBe listOf("products")

        val properties = schemaDefinition.properties
        properties shouldHaveSize 2

        // Check the products array property
        val productsProperty = properties["products"]!!
        productsProperty.type shouldBe "array"
        productsProperty.description shouldBe "List of products"

        // Check the items definition (product objects)
        val productItemDefinition = productsProperty.items!!
        productItemDefinition.type shouldBe "object"
        productItemDefinition.properties.shouldNotBeNull()
        productItemDefinition.properties shouldHaveSize (4)
        productItemDefinition.required shouldBe listOf("id", "name", "price")

        // Check nested array in product
        val categoriesProperty = productItemDefinition.properties["categories"]!!
        categoriesProperty.type shouldBe "array"
        categoriesProperty.items.shouldNotBeNull {
            type shouldBe "string"
        }

        // Check pagination object
        val paginationProperty = properties["pagination"]!!
        paginationProperty.type shouldBe "object"
        paginationProperty.properties.shouldNotBeNull()
        paginationProperty.properties shouldHaveSize 3

        val pageSizeProperty = paginationProperty.properties["pageSize"]!!
        pageSizeProperty.type shouldBe "integer"
        pageSizeProperty.minimum shouldBe 1.0
        pageSizeProperty.maximum shouldBe 100.0
    }

    @Test
    fun `Should deserialize schema with references`() {
        val json =
            $$"""
            {
              "name": "BlogPost",
              "schema": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "string"
                  },
                  "title": {
                    "type": "string",
                    "minLength": 5,
                    "maxLength": 100
                  },
                  "author": {
                    "$ref": "#/definitions/Author"
                  },
                  "comments": {
                    "type": "array",
                    "items": {
                      "$ref": "#/definitions/Comment"
                    }
                  }
                },
                "required": ["id", "title", "author"]
              }
            }
            """.trimIndent()

        val schema = jsonParser.decodeFromString<JsonSchema>(json)

        schema.name shouldBe "BlogPost"

        val properties = schema.schema.properties
        properties shouldHaveSize 4

        // Check reference property
        val authorProperty = properties["author"]!!
        authorProperty.type.shouldBeNull() // $ref properties don't need a type
        authorProperty.ref shouldBe "#/definitions/Author"

        // Check array with reference items
        val commentsProperty = properties["comments"]!!
        commentsProperty.type shouldBe "array"
        commentsProperty.items.shouldNotBeNull()
        commentsProperty.items.type.shouldBeNull()
        commentsProperty.items.ref shouldBe "#/definitions/Comment"
    }
}
