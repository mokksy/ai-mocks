package me.kpavlov.mokksy.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class StringsTest {
    @ParameterizedTest
    @CsvSource(
        ",",
        "abcdefghij, abcdefghij",
        "abcdefghi, abcdefghi",
        "abcdefghijklmnopqrst, abcd...rst",
    )
    fun testShortStringUnchanged(
        input: String?,
        expected: String?,
    ) {
        val result = input.ellipsizeMiddle(10)
        result shouldBe expected
    }
}
