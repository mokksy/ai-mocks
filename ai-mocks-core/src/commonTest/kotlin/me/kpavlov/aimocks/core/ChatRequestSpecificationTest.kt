package me.kpavlov.aimocks.core

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatRequestSpecificationTest {
    lateinit var subject: ModelRequestSpecification<String>

    @BeforeTest
    fun before() {
        subject =
            object : ModelRequestSpecification<String>() {
                override fun systemMessageContains(substring: String) = TODO()

                override fun userMessageContains(substring: String) = TODO()
            }
    }

    @Test
    fun requestBodyDoesNotContainsIgnoringCase() {
        subject.requestBodyDoesNotContainsIgnoringCase("hello world")

        subject.requestBodyString.first().let {
            it.test("Bye Space") shouldNotBeNull {
                passed() shouldBe true
            }
            it.test("so, hello world etc") shouldNotBeNull {
                passed() shouldBe false
                failureMessage() shouldBe
                    "\"so, hello world etc\" should not contain the substring \"hello world\" (case insensitive)"
                negatedFailureMessage() shouldBe
                    "\"so, hello world etc\" should contain the substring \"hello world\" (case insensitive)"
            }
        }
    }

    @Test
    fun requestBodyDoesNotContains() {
        subject.requestBodyDoesNotContains("hello world")

        subject.requestBodyString.first().let {
            it.test("so, hello world etc") shouldNotBeNull {
                passed() shouldBe false
                failureMessage() shouldBe
                    "\"so, hello world etc\" should not contain the substring \"hello world\" (case sensitive)"
                negatedFailureMessage() shouldBe
                    "\"so, hello world etc\" should contain the substring \"hello world\" (case sensitive)"
            }
            it.test("hello people and world") shouldNotBeNull {
                passed() shouldBe true
            }
        }
    }
}
