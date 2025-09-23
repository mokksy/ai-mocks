package me.kpavlov.aimocks.openai.official.moderations

import com.openai.errors.BadRequestException
import com.openai.models.moderations.ModerationCreateParams
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import me.kpavlov.aimocks.openai.model.moderation.InputType
import me.kpavlov.aimocks.openai.model.moderation.InputType.TEXT
import me.kpavlov.aimocks.openai.model.moderation.ModerationCategory
import me.kpavlov.aimocks.openai.official.AbstractOpenaiTest
import me.kpavlov.aimocks.openai.openai
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

internal class ModerationOpenaiTest : AbstractOpenaiTest() {
    @Test
    @Suppress("LongMethod")
    fun `Should respond to Moderation`() {
        openai.moderation {
            model = "omni-moderation-latest"
            inputContains("Hello world")
        } responds {
            // simple flagged result
            flagged = true
            delay = 200.milliseconds
            category(name = "harassment", score = 0.1, inputTypes = listOf(TEXT))
            category(
                name = ModerationCategory.SEXUAL,
                score = 0.2,
                inputTypes =
                    listOf(
                        TEXT,
                        InputType.IMAGE,
                    ),
            )
        }

        val params =
            ModerationCreateParams
                .builder()
                .model("omni-moderation-latest")
                .input("Hello world")
                .build()

        val timedValue =
            measureTimedValue {
                client
                    .moderations()
                    .create(params)
            }

        timedValue.duration shouldBeGreaterThanOrEqualTo 200.milliseconds
        val result = timedValue.value

        result.model() shouldBe "omni-moderation-latest"
        result.results() shouldHaveSize 1
        val res = result.results().first()
        res.flagged().shouldBeTrue()

        // Verify ModerationResult fields: categories, scores, applied input types
        res.categories() shouldNotBeNull {
            harassment() shouldBe true
            harassmentThreatening() shouldBe false
            sexual() shouldBe true
            hate() shouldBe false
            hateThreatening() shouldBe false
            illicit().get() shouldBe false
            illicitViolent().get() shouldBe false
            selfHarmIntent() shouldBe false
            selfHarmInstructions() shouldBe false
            selfHarm() shouldBe false
            sexualMinors() shouldBe false
            violence() shouldBe false
            violenceGraphic() shouldBe false
        }

        res.categoryScores() shouldNotBeNull {
            harassment() shouldBe 0.1
            harassmentThreatening() shouldBe 0.0
            sexual() shouldBe 0.2
            hate() shouldBe 0.0
            hateThreatening() shouldBe 0.0
            illicit() shouldBe 0.0
            illicitViolent() shouldBe 0.0
            selfHarmIntent() shouldBe 0.0
            selfHarmInstructions() shouldBe 0.0
            selfHarm() shouldBe 0.0
            sexualMinors() shouldBe 0.0
            violence() shouldBe 0.0
            violenceGraphic() shouldBe 0.0
        }

        res.categoryAppliedInputTypes() shouldNotBeNull {
            harassment().map { it.toString() }.toSet() shouldBe setOf("text")
            harassmentThreatening().map { it.toString() }.toSet() shouldBe setOf("text")
            sexual().map { it.toString() }.toSet() shouldBe setOf("text", "image")
            hate().map { it.toString() }.toSet() shouldBe setOf("text")
            hateThreatening().map { it.toString() }.toSet() shouldBe setOf("text")
            illicit().map { it.toString() }.toSet() shouldBe setOf("text")
            illicitViolent().map { it.toString() }.toSet() shouldBe setOf("text")
            selfHarmIntent().map { it.toString() }.toSet() shouldBe setOf("text")
            selfHarmInstructions().map { it.toString() }.toSet() shouldBe
                setOf(
                    "text",
                )
            selfHarm().map { it.toString() }.toSet() shouldBe setOf("text")
            sexualMinors().map { it.toString() }.toSet() shouldBe setOf("text")
            violence().map { it.toString() }.toSet() shouldBe setOf("text")
            val vg = violenceGraphic()
            vg shouldHaveSize 1
            vg.map { it.toString() }.toSet() shouldBe setOf("image")
        }
    }

    @Test
    fun `Should respond with unexpected error for Moderation`() {
        openai
            .moderation {
                model = modelName
                inputContains("boom")
            }.respondsError(String::class) {
                body = "Kaboom!"
                contentType = ContentType.Text.Plain
                httpStatus = HttpStatusCode.BadRequest
                delay = 200.milliseconds
            }

        val params =
            ModerationCreateParams
                .builder()
                .model(modelName)
                .input("boom")
                .build()

        val timedValue =
            measureTimedValue {
                shouldThrow<BadRequestException> {
                    client
                        .moderations()
                        .create(params)
                }
            }

        timedValue.duration shouldBeGreaterThan 200.milliseconds
        val exception = timedValue.value
        exception.statusCode() shouldBe HttpStatusCode.BadRequest.value
    }
}
