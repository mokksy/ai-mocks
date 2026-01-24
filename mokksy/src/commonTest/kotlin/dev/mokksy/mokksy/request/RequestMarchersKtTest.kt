package dev.mokksy.mokksy.request

import dev.mokksy.mokksy.Input
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlin.test.Test

class RequestMarchersKtTest {
    private val predicate: (Input?) -> Boolean =
        object : (Input?) -> Boolean {
            override fun invoke(p1: Input?): Boolean = (p1?.name == "foo")

            override fun toString(): String = "predicateToString"
        }

    @Test
    fun `Should test predicate matcher`() {
        val input = Input("foo")

        val matcher = predicateMatcher<Input>("my predicate description", predicate)

        matcher.toString() shouldBe "my predicate description"
        val testResult = matcher.test(input)

        assertSoftly(testResult) {
            passed() shouldBe true
            failureMessage() shouldBe
                "Object 'Input(name=foo)' should match predicate 'predicateToString'"
            negatedFailureMessage() shouldBe
                "Object 'Input(name=foo)' should NOT match predicate 'predicateToString'"
        }
    }

    @Test
    fun `Should test successCallMatcher`() {
        val input = Input("foo")

        val matcher = successCallMatcher<Input>("Should not be null") { input.shouldNotBeNull() }

        matcher.toString() shouldStartWith "Should not be null"
        val testResult = matcher.test(input)

        assertSoftly(testResult) {
            passed() shouldBe true
            failureMessage() shouldStartWith
                "Object 'Input(name=foo)' should satisfy '"
            negatedFailureMessage() shouldStartWith
                "Object 'Input(name=foo)' should NOT satisfy '"
        }
    }
}
