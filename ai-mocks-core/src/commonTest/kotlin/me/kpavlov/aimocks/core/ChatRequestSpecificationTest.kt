package me.kpavlov.aimocks.core

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.include
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
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

    @Test
    fun requestMatches() {
        subject.requestMatches(include("world"))

        subject.requestBody.first().let {
            it.test("so, hello world etc") shouldNotBeNull {
                passed() shouldBe true
                failureMessage() shouldBe
                    "\"so, hello world etc\" should include substring \"world\""
                negatedFailureMessage() shouldStartWith
                    "\"so, hello world etc\" should not include substring \"world\""
            }
            it.test("hello people") shouldNotBeNull {
                passed() shouldBe false
            }
        }
    }

    @Test
    fun requestMatchesPredicate() {
        subject.requestMatchesPredicate {
            it.contains("hello world")
        }

        subject.requestBody.first().let {
            it.test("so, hello world etc") shouldNotBeNull {
                passed() shouldBe true
                failureMessage() shouldStartWith
                    "Object 'so, hello world etc' should match predicate "
                negatedFailureMessage() shouldStartWith
                    "Object 'so, hello world etc' should NOT match predicate "
            }
            it.test("hello people and world") shouldNotBeNull {
                passed() shouldBe false
            }
        }
    }

    @Test
    fun requestSatisfies() {
        subject.requestSatisfies { it shouldContain "world" }

        subject.requestBody.first().let {
            it.test("so, hello world etc") shouldNotBeNull {
                passed() shouldBe true
                failureMessage() shouldStartWith
                    "Object 'so, hello world etc' should satisfy '"
                negatedFailureMessage() shouldStartWith
                    "Object 'so, hello world etc' should NOT satisfy '"
            }
            it.test("hello people") shouldNotBeNull {
                passed() shouldBe false
            }
        }
    }
}
