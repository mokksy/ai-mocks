package me.kpavlov.mokksy.request

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import me.kpavlov.mokksy.Input
import org.junit.jupiter.api.Test

class RequestMarchersKtTest {
    private val predicate: (Input?) -> Boolean =
        object : (Input?) -> Boolean {
            override fun invoke(p1: Input?): Boolean = (p1?.name == "foo")

            override fun toString(): String = "predicateToString"
        }

    @Test
    fun `Should test predicate matcher`() {
        val input = Input("foo")

        predicateMatcher<Input>(predicate)
            .apply {
                toString() shouldBe "PredicateMatcher(predicateToString)"
                test(input).apply {
                    passed() shouldBe true
                    failureMessage() shouldBe
                        "Object 'Input(name=foo)' should match predicate 'predicateToString'"
                    negatedFailureMessage() shouldBe
                        "Object 'Input(name=foo)' should NOT match predicate 'predicateToString'"
                }
            }
    }@Test
    fun `Should test successCallMatcher`() {
        val input = Input("foo")

        successCallMatcher<Input>{ input.shouldNotBeNull() }
            .apply {
                toString() shouldStartWith "successCallMatcher("
                test(input).apply {
                    passed() shouldBe true
                    failureMessage() shouldStartWith
                        "Object 'Input(name=foo)' should satisfy '"
                    negatedFailureMessage() shouldStartWith
                        "Object 'Input(name=foo)' should NOT satisfy '"
                }
            }
    }
}
