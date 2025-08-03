package me.kpavlov.mokksy.utils

import assertk.assertThat
import assertk.assertions.*
import kotlin.test.Test

/**
 * Comprehensive unit tests for the String.ellipsizeMiddle extension function.
 * Testing framework: kotlin.test with assertk assertions (following project conventions).
 */
class StringsTest {

    // Tests for ellipsizeMiddle function - null handling
    @Test
    fun `ellipsizeMiddle should return null when input is null`() {
        val result: String? = null
        assertThat(result.ellipsizeMiddle(10)).isNull()
    }

    @Test
    fun `ellipsizeMiddle should return null when input is null regardless of maxLength`() {
        val result: String? = null
        assertThat(result.ellipsizeMiddle(5)).isNull()
        assertThat(result.ellipsizeMiddle(100)).isNull()
        assertThat(result.ellipsizeMiddle(0)).isNull()
        assertThat(result.ellipsizeMiddle(-1)).isNull()
    }

    // Tests for ellipsizeMiddle function - length conditions
    @Test
    fun `ellipsizeMiddle should return original string when length is less than or equal to maxLength`() {
        assertThat("hello".ellipsizeMiddle(5)).isEqualTo("hello")
        assertThat("hello".ellipsizeMiddle(6)).isEqualTo("hello")
        assertThat("hello".ellipsizeMiddle(10)).isEqualTo("hello")
        assertThat("test".ellipsizeMiddle(4)).isEqualTo("test")
        assertThat("a".ellipsizeMiddle(1)).isEqualTo("a")
        assertThat("".ellipsizeMiddle(0)).isEqualTo("")
        assertThat("".ellipsizeMiddle(5)).isEqualTo("")
    }

    // Tests for ellipsizeMiddle function - maxLength less than 5
    @Test
    fun `ellipsizeMiddle should return original string when maxLength is less than 5`() {
        assertThat("hello world".ellipsizeMiddle(4)).isEqualTo("hello world")
        assertThat("hello world".ellipsizeMiddle(3)).isEqualTo("hello world")
        assertThat("hello world".ellipsizeMiddle(2)).isEqualTo("hello world")
        assertThat("hello world".ellipsizeMiddle(1)).isEqualTo("hello world")
        assertThat("hello world".ellipsizeMiddle(0)).isEqualTo("hello world")
        assertThat("very long string that needs truncation".ellipsizeMiddle(-1)).isEqualTo("very long string that needs truncation")
    }

    // Tests for ellipsizeMiddle function - basic truncation
    @Test
    fun `ellipsizeMiddle should truncate string with ellipsis when conditions are met`() {
        // "hello world" (11 chars) -> maxLength 8 -> "he...ld" (7 chars)
        assertThat("hello world".ellipsizeMiddle(8)).isEqualTo("he...ld")
        
        // "hello world" (11 chars) -> maxLength 7 -> "he...ld" (7 chars)
        assertThat("hello world".ellipsizeMiddle(7)).isEqualTo("he...ld")
        
        // "hello world" (11 chars) -> maxLength 6 -> "h...ld" (6 chars)
        assertThat("hello world".ellipsizeMiddle(6)).isEqualTo("h...ld")
        
        // "hello world" (11 chars) -> maxLength 5 -> "h...d" (5 chars)
        assertThat("hello world".ellipsizeMiddle(5)).isEqualTo("h...d")
    }

    // Tests for ellipsizeMiddle function - even maxLength
    @Test
    fun `ellipsizeMiddle should handle even maxLength correctly`() {
        // maxLength = 8: (8-3)/2 = 2.5 -> 2, start gets extra char for odd remainder
        // "hello world" -> "he" + "..." + "ld" = "he...ld"
        assertThat("hello world".ellipsizeMiddle(8)).isEqualTo("he...ld")
        
        // maxLength = 10: (10-3)/2 = 3.5 -> 3, start gets extra char
        // "hello world" -> "hel" + "..." + "rld" = "hel...rld"
        assertThat("hello world".ellipsizeMiddle(10)).isEqualTo("hel...rld")
        
        // maxLength = 6: (6-3)/2 = 1.5 -> 1, start gets extra char
        // "hello world" -> "h" + "..." + "d" = "h...d"
        assertThat("hello world".ellipsizeMiddle(6)).isEqualTo("h...d")
    }

    // Tests for ellipsizeMiddle function - odd maxLength
    @Test
    fun `ellipsizeMiddle should handle odd maxLength correctly`() {
        // maxLength = 7: (7-3)/2 = 2, no remainder
        // "hello world" -> "he" + "..." + "ld" = "he...ld"
        assertThat("hello world".ellipsizeMiddle(7)).isEqualTo("he...ld")
        
        // maxLength = 9: (9-3)/2 = 3, no remainder
        // "hello world" -> "hel" + "..." + "rld" = "hel...rld"
        assertThat("hello world".ellipsizeMiddle(9)).isEqualTo("hel...rld")
        
        // maxLength = 5: (5-3)/2 = 1, no remainder
        // "hello world" -> "h" + "..." + "d" = "h...d"
        assertThat("hello world".ellipsizeMiddle(5)).isEqualTo("h...d")
    }

    // Tests for ellipsizeMiddle function - edge cases
    @Test
    fun `ellipsizeMiddle should handle single character strings`() {
        assertThat("a".ellipsizeMiddle(5)).isEqualTo("a")
        assertThat("a".ellipsizeMiddle(1)).isEqualTo("a")
        assertThat("a".ellipsizeMiddle(0)).isEqualTo("a")
    }

    @Test
    fun `ellipsizeMiddle should handle empty strings`() {
        assertThat("".ellipsizeMiddle(5)).isEqualTo("")
        assertThat("".ellipsizeMiddle(0)).isEqualTo("")
        assertThat("".ellipsizeMiddle(10)).isEqualTo("")
    }

    @Test
    fun `ellipsizeMiddle should handle strings exactly at boundary lengths`() {
        // String of length exactly 5 with maxLength 5
        assertThat("12345".ellipsizeMiddle(5)).isEqualTo("12345")
        
        // String of length exactly 6 with maxLength 6
        assertThat("123456".ellipsizeMiddle(6)).isEqualTo("123456")
        
        // String of length exactly 4 with maxLength 5 (no truncation needed)
        assertThat("1234".ellipsizeMiddle(5)).isEqualTo("1234")
    }

    // Tests for ellipsizeMiddle function - whitespace and special characters
    @Test
    fun `ellipsizeMiddle should handle strings with whitespace`() {
        assertThat("hello   world".ellipsizeMiddle(8)).isEqualTo("he...rld")
        assertThat("  hello world  ".ellipsizeMiddle(8)).isEqualTo("  ...d  ")
        assertThat("\thello\tworld\n".ellipsizeMiddle(8)).isEqualTo("\th...ld\n")
    }

    @Test
    fun `ellipsizeMiddle should handle strings with special characters`() {
        assertThat("hello@#$world".ellipsizeMiddle(8)).isEqualTo("he...rld")
        assertThat("!@#$%^&*()".ellipsizeMiddle(7)).isEqualTo("!@...*()")
        assertThat("hello/world\\test".ellipsizeMiddle(10)).isEqualTo("hel...test")
    }

    @Test
    fun `ellipsizeMiddle should handle strings with numbers`() {
        assertThat("123456789".ellipsizeMiddle(6)).isEqualTo("1...89")
        assertThat("abc123def456ghi".ellipsizeMiddle(10)).isEqualTo("abc...6ghi")
    }

    // Tests for ellipsizeMiddle function - unicode characters
    @Test
    fun `ellipsizeMiddle should handle unicode characters`() {
        assertThat("hello 世界 world".ellipsizeMiddle(10)).isEqualTo("hel...orld")
        assertThat("🌍🌎🌏🌍🌎🌏".ellipsizeMiddle(8)).isEqualTo("🌍🌎...🌎🌏")
        assertThat("café naïve résumé".ellipsizeMiddle(10)).isEqualTo("caf...sumé")
    }

    // Tests for ellipsizeMiddle function - very long strings
    @Test
    fun `ellipsizeMiddle should handle very long strings`() {
        val longString = "a".repeat(1000)
        val result = longString.ellipsizeMiddle(20)
        assertThat(result).hasLength(20)
        assertThat(result).startsWith("aaaaaaaa")
        assertThat(result).contains("...")
        assertThat(result).endsWith("aaaaaaaa")
        
        val veryLongString = "x".repeat(10000)
        val shortResult = veryLongString.ellipsizeMiddle(5)
        assertThat(shortResult).isEqualTo("x...x")
    }

    // Tests for ellipsizeMiddle function - different content patterns
    @Test
    fun `ellipsizeMiddle should handle mixed alphanumeric content`() {
        assertThat("ABC123def456GHI".ellipsizeMiddle(8)).isEqualTo("AB...GHI")
        assertThat("test_file_name_123.txt".ellipsizeMiddle(15)).isEqualTo("test_f...23.txt")
        assertThat("user@example.com".ellipsizeMiddle(12)).isEqualTo("user...e.com")
    }

    @Test
    fun `ellipsizeMiddle should handle URL-like strings`() {
        assertThat("https://example.com/very/long/path".ellipsizeMiddle(20)).isEqualTo("https://...ng/path")
        assertThat("file:///path/to/document.pdf".ellipsizeMiddle(15)).isEqualTo("file:///...ent.pdf")
    }

    @Test
    fun `ellipsizeMiddle should handle code-like strings`() {
        assertThat("com.example.package.ClassName".ellipsizeMiddle(18)).isEqualTo("com.exa...assName")
        assertThat("function_with_very_long_name()".ellipsizeMiddle(20)).isEqualTo("functio...g_name()")
    }

    // Tests for ellipsizeMiddle function - performance and stress testing
    @Test
    fun `ellipsizeMiddle should handle repeated operations efficiently`() {
        val testString = "This is a test string for performance testing"
        repeat(1000) {
            val result = testString.ellipsizeMiddle(20)
            assertThat(result).hasLength(20)
            assertThat(result).contains("...")
        }
    }

    @Test
    fun `ellipsizeMiddle should maintain consistent results`() {
        val testString = "consistent test string for validation"
        val result1 = testString.ellipsizeMiddle(15)
        val result2 = testString.ellipsizeMiddle(15)
        val result3 = testString.ellipsizeMiddle(15)
        
        assertThat(result1).isEqualTo(result2)
        assertThat(result2).isEqualTo(result3)
        assertThat(result1).isEqualTo("consis...ation")
    }

    // Tests for ellipsizeMiddle function - boundary value analysis
    @Test
    fun `ellipsizeMiddle should handle maxLength of exactly 5`() {
        assertThat("123456789".ellipsizeMiddle(5)).isEqualTo("1...9")
        assertThat("abcdefghijklmnop".ellipsizeMiddle(5)).isEqualTo("a...p")
        assertThat("short".ellipsizeMiddle(5)).isEqualTo("short")
    }

    @Test
    fun `ellipsizeMiddle should handle maxLength just above minimum`() {
        assertThat("123456789".ellipsizeMiddle(6)).isEqualTo("1...89")
        assertThat("abcdefghijklmnop".ellipsizeMiddle(7)).isEqualTo("ab...op")
        assertThat("testing string".ellipsizeMiddle(8)).isEqualTo("te...ing")
    }

    // Tests for ellipsizeMiddle function - mathematical correctness
    @Test
    fun `ellipsizeMiddle should produce correct length results`() {
        val testCases = listOf(
            "hello world" to 8,
            "testing this function" to 12,
            "very long string indeed" to 15,
            "short" to 10,
            "a" to 5
        )
        
        testCases.forEach { (input, maxLen) ->
            val result = input.ellipsizeMiddle(maxLen)
            if (input.length > maxLen && maxLen >= 5) {
                assertThat(result).hasLength(maxLen)
                assertThat(result).contains("...")
            } else {
                assertThat(result).isEqualTo(input)
            }
        }
    }

    @Test
    fun `ellipsizeMiddle should maintain start and end portions correctly`() {
        val input = "0123456789ABCDEF"
        val result = input.ellipsizeMiddle(10) // Should be "012...DEF" or "0123...EF"
        
        assertThat(result).hasLength(10)
        assertThat(result).contains("...")
        assertThat(result).startsWith("0")
        assertThat(result).endsWith("F")
        
        // Verify the mathematical distribution
        val parts = result.split("...")
        assertThat(parts).hasSize(2)
        val startPart = parts[0]
        val endPart = parts[1]
        assertThat(startPart.length + endPart.length + 3).isEqualTo(10)
    }
}