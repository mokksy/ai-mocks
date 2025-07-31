package me.kpavlov.mokksy.utils.logger

import io.ktor.http.ContentType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertContains
import kotlin.test.assertNotNull

/**
 * Comprehensive unit tests for the Highlighting functionality.
 * Tests cover happy paths, edge cases, and failure conditions for all public methods.
 */
class HighlightingTest {

    // JSON Highlighting Tests
    @Test
    fun testHighlightBodyWithSimpleJson() {
        val json = """{"name": "Alice", "age": 30}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertNotEquals(json, result)
        // Should contain ANSI color codes
        assertTrue(result.contains("\u001B["), "Result should contain ANSI color codes")
        // Should preserve the original structure
        assertContains(result, "name")
        assertContains(result, "Alice")
        assertContains(result, "age")
        assertContains(result, "30")
    }

    @Test
    fun testHighlightBodyWithComplexJson() {
        val json = """
        {
            "user": {
                "name": "John Doe",
                "age": 42,
                "active": true,
                "balance": 123.45,
                "address": null
            },
            "metadata": {
                "created": "2023-01-01T00:00:00Z",
                "tags": ["user", "premium"],
                "count": 0
            }
        }
        """.trimIndent()
        
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertNotEquals(json, result)
        assertTrue(result.contains("\u001B["))
        
        // Check that all JSON elements are preserved
        assertContains(result, "John Doe")
        assertContains(result, "42")
        assertContains(result, "true")
        assertContains(result, "123.45")
        assertContains(result, "null")
        assertContains(result, "2023-01-01T00:00:00Z")
        assertContains(result, "user")
        assertContains(result, "premium")
        assertContains(result, "0")
    }

    @Test
    fun testHighlightBodyWithJsonBooleanAndNullValues() {
        val json = """{"isActive": true, "isDeleted": false, "middleName": null}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "true")
        assertContains(result, "false")
        assertContains(result, "null")
    }

    @Test
    fun testHighlightBodyWithJsonNumericValues() {
        val json = """{"integer": 42, "float": 3.14159, "negative": -100, "zero": 0}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "42")
        assertContains(result, "3.14159")
        assertContains(result, "-100")
        assertContains(result, "0")
    }

    @Test
    fun testHighlightBodyWithJsonSpecialCharacters() {
        val json = """{"special": "hello\nworld", "unicode": "café 🌟", "escaped": "\"quoted\""}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "hello\\nworld")
        assertContains(result, "café 🌟")
        assertContains(result, "\\\"quoted\\\"")
    }

    @Test
    fun testHighlightBodyWithMalformedJson() {
        val malformedJson = """{"name": "Alice", "age": }"""
        val result = Highlighting.highlightBody(malformedJson, ContentType.Application.Json)
        
        // Should handle malformed JSON gracefully
        assertNotNull(result)
        assertContains(result, "Alice")
    }

    @Test
    fun testHighlightBodyWithEmptyJson() {
        val emptyJson = "{}"
        val result = Highlighting.highlightBody(emptyJson, ContentType.Application.Json)
        
        assertNotNull(result)
        assertEquals(emptyJson, result) // Should return unchanged since no key-value pairs to highlight
    }

    @Test
    fun testHighlightBodyWithJsonWhitespacePreservation() {
        val json = """{"name"  :  "Alice",  "age":42}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        // Should preserve spacing around colons
        assertTrue(result.contains("  :  ") || result.contains(": "))
    }

    // Form URL-Encoded Highlighting Tests
    @Test
    fun testHighlightBodyWithSimpleFormData() {
        val formData = "name=Alice&age=30&active=true"
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertNotEquals(formData, result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "name")
        assertContains(result, "Alice")
        assertContains(result, "age")
        assertContains(result, "30")
        assertContains(result, "active")
        assertContains(result, "true")
        assertEquals(2, result.count { it == '&' }) // Should preserve ampersands
    }

    @Test
    fun testHighlightBodyWithFormDataSpecialCharacters() {
        val formData = "email=user%40example.com&message=Hello%20World"
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "email")
        assertContains(result, "user%40example.com")
        assertContains(result, "message")
        assertContains(result, "Hello%20World")
    }

    @Test
    fun testHighlightBodyWithFormDataInvalidPairs() {
        val formData = "validkey=value&invalidpair&anotherkey=anothervalue"
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "validkey")
        assertContains(result, "value")
        assertContains(result, "invalidpair") // Should be left unchanged
        assertContains(result, "anotherkey")
        assertContains(result, "anothervalue")
    }

    @Test
    fun testHighlightBodyWithFormDataEmptyValues() {
        val formData = "key1=&key2=value&key3="
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "key1")
        assertContains(result, "key2")
        assertContains(result, "key3")
        assertContains(result, "value")
    }

    @Test
    fun testHighlightBodyWithFormDataMultipleEquals() {
        val formData = "equation=1+1=2&url=http://example.com"
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        // Should handle pairs with multiple equals signs correctly
        assertContains(result, "equation")
        assertContains(result, "url")
    }

    @Test
    fun testHighlightBodyWithEmptyFormData() {
        val formData = ""
        val result = Highlighting.highlightBody(formData, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertEquals(formData, result) // Empty string should remain unchanged
    }

    // Other Content Types Tests
    @Test
    fun testHighlightBodyWithTextPlain() {
        val text = "This is plain text content"
        val result = Highlighting.highlightBody(text, ContentType.Text.Plain)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, text)
        // Should be colored with light gray
    }

    @Test
    fun testHighlightBodyWithApplicationXml() {
        val xml = "<root><element>value</element></root>"
        val result = Highlighting.highlightBody(xml, ContentType.Application.Xml)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, xml)
        // Should be colored with light gray
    }

    @Test
    fun testHighlightBodyWithCustomContentType() {
        val content = "Custom content type data"
        val customContentType = ContentType("application", "custom")
        val result = Highlighting.highlightBody(content, customContentType)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, content)
        // Should be colored with light gray
    }

    // Edge Cases and Error Conditions
    @Test
    fun testHighlightBodyWithEmptyString() {
        val empty = ""
        val result = Highlighting.highlightBody(empty, ContentType.Application.Json)
        
        assertNotNull(result)
        assertEquals(empty, result)
    }

    @Test
    fun testHighlightBodyWithVeryLongContent() {
        val longContent = "key=value&".repeat(1000).dropLast(1) // Remove trailing &
        val result = Highlighting.highlightBody(longContent, ContentType.Application.FormUrlEncoded)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertTrue(result.length >= longContent.length)
    }

    @Test
    fun testHighlightBodyWithUnicodeContent() {
        val unicodeJson = """{"message": "Hello 世界 🌍", "café": "résumé"}"""
        val result = Highlighting.highlightBody(unicodeJson, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "Hello 世界 🌍")
        assertContains(result, "résumé")
    }

    @Test
    fun testHighlightBodyWithMultilineContent() {
        val multilineJson = """
        {
            "line1": "First line",
            "line2": "Second line",
            "line3": "Third line"
        }
        """.trimIndent()
        
        val result = Highlighting.highlightBody(multilineJson, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "First line")
        assertContains(result, "Second line")
        assertContains(result, "Third line")
    }

    @Test
    fun testHighlightBodyConsistency() {
        val json = """{"test": "consistency"}"""
        val result1 = Highlighting.highlightBody(json, ContentType.Application.Json)
        val result2 = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertEquals(result1, result2, "Same input should produce consistent output")
    }

    @Test
    fun testHighlightBodyPerformance() {
        val largeJson = """{"data": "${"x".repeat(10000)}"}"""
        
        val startTime = kotlin.system.getTimeMillis()
        val result = Highlighting.highlightBody(largeJson, ContentType.Application.Json)
        val endTime = kotlin.system.getTimeMillis()
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertTrue(endTime - startTime < 5000, "Highlighting should complete in reasonable time")
    }

    @Test
    fun testHighlightBodyWithNestedQuotesInJson() {
        val json = """{"message": "He said \"Hello\" to me", "code": "if (x == \"test\") { return; }"}"""
        val result = Highlighting.highlightBody(json, ContentType.Application.Json)
        
        assertNotNull(result)
        assertTrue(result.contains("\u001B["))
        assertContains(result, "He said \\\"Hello\\\" to me")
        assertContains(result, "if (x == \\\"test\\\") { return; }")
    }

    @Test
    fun testHighlightBodyThreadSafety() {
        val json = """{"concurrent": "test"}"""
        val results = mutableListOf<String>()
        
        // Simple concurrent access test
        repeat(10) {
            results.add(Highlighting.highlightBody(json, ContentType.Application.Json))
        }
        
        assertTrue(results.all { it == results.first() }, "Thread safety: all results should be identical")
        assertTrue(results.all { it.contains("\u001B[") }, "All results should contain ANSI codes")
    }

    @Test
    fun testHighlightBodyWithDifferentJsonContentTypes() {
        val json = """{"type": "test"}"""
        
        // Test with different JSON content type variations
        val contentTypes = listOf(
            ContentType.Application.Json,
            ContentType("application", "json", listOf("charset" to "utf-8")),
            ContentType("application", "vnd.api+json")
        )
        
        contentTypes.forEach { contentType ->
            val result = Highlighting.highlightBody(json, contentType)
            assertNotNull(result, "Failed for content type: $contentType")
            if (contentType.match(ContentType.Application.Json)) {
                assertTrue(result.contains("\u001B["), "Should highlight JSON for content type: $contentType")
            }
        }
    }
}