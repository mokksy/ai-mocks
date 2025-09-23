package me.kpavlov.aimocks.openai.model.moderations

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.openai.model.moderation.CreateModerationRequest
import me.kpavlov.aimocks.openai.model.moderation.InputType.IMAGE
import me.kpavlov.aimocks.openai.model.moderation.InputType.TEXT
import me.kpavlov.aimocks.openai.model.moderation.Moderation
import me.kpavlov.aimocks.openai.model.moderation.ModerationCategory
import org.junit.jupiter.api.Test

internal class ModerationModelsTest {
    private val jsonParser =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

    /**
     * https://platform.openai.com/docs/api-reference/moderations/create
     */
    @Test
    fun `Should deserialize simple ModerationRequest`() {
        // language=json
        val json =
            """
            {
                "input": "Karamba!"
              }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateModerationRequest>(json)

        request shouldNotBeNull {
        }
        assertSoftly(request) {
            it.input shouldBe listOf("Karamba!")
            it.model shouldBe null
        }
    }

    @Test
    fun `Should deserialize full ModerationRequest`() {
        // todo: support complex input
        // language=json
        val json =
            """
            {
                "model": "super-model",
                "input": "Karamba!"
              }
            """.trimIndent()

        val request = jsonParser.decodeFromString<CreateModerationRequest>(json)

        request shouldNotBeNull {
        }
        assertSoftly(request) {
            it.input shouldBe listOf("Karamba!")
            it.model shouldBe "super-model"
        }
    }

    @Test
    @Suppress("LongMethod")
    fun `Should deserialize ModerationResponse`() {
        // language=json
        val json =
            """
            {
              "id": "modr-0d9740456c391e43c445bf0f010940c7",
              "model": "omni-moderation-latest",
              "results": [
                {
                  "flagged": true,
                  "categories": {
                    "harassment": true,
                    "harassment/threatening": true,
                    "sexual": false,
                    "hate": false,
                    "hate/threatening": false,
                    "illicit": false,
                    "illicit/violent": false,
                    "self-harm/intent": false,
                    "self-harm/instructions": false,
                    "self-harm": false,
                    "sexual/minors": false,
                    "violence": true,
                    "violence/graphic": true
                  },
                  "category_scores": {
                    "harassment": 0.8189693396524255,
                    "harassment/threatening": 0.804985420696006,
                    "sexual": 1.573112165348997e-6,
                    "hate": 0.007562942636942845,
                    "hate/threatening": 0.004208854591835476,
                    "illicit": 0.030535955153511665,
                    "illicit/violent": 0.008925306722380033,
                    "self-harm/intent": 0.00023023930975076432,
                    "self-harm/instructions": 0.0002293869201073356,
                    "self-harm": 0.012598046106750154,
                    "sexual/minors": 2.212566909570261e-8,
                    "violence": 0.9999992735124786,
                    "violence/graphic": 0.843064871157054
                  },
                  "category_applied_input_types": {
                    "harassment": [
                      "text"
                    ],
                    "harassment/threatening": [
                      "text"
                    ],
                    "sexual": [
                      "text",
                      "image"
                    ],
                    "hate": [
                      "text"
                    ],
                    "hate/threatening": [
                      "text"
                    ],
                    "illicit": [
                      "text"
                    ],
                    "illicit/violent": [
                      "text"
                    ],
                    "self-harm/intent": [
                      "text",
                      "image"
                    ],
                    "self-harm/instructions": [
                      "text",
                      "image"
                    ],
                    "self-harm": [
                      "text",
                      "image"
                    ],
                    "sexual/minors": [
                      "text"
                    ],
                    "violence": [
                      "text",
                      "image"
                    ],
                    "violence/graphic": [
                      "text",
                      "image"
                    ]
                  }
                }
              ]
            }
            """.trimIndent()

        val moderation = jsonParser.decodeFromString<Moderation>(json)

        moderation shouldNotBeNull {
            assertSoftly(moderation) {
                id shouldBe "modr-0d9740456c391e43c445bf0f010940c7"
                model shouldBe "omni-moderation-latest"
                results.size shouldBe 1

                results.first() shouldNotBeNull {
                    flagged shouldBe true

                    categories[ModerationCategory.HARASSMENT] shouldBe true
                    categories[ModerationCategory.HARASSMENT_THREATENING] shouldBe true
                    categories[ModerationCategory.SEXUAL] shouldBe false
                    categories[ModerationCategory.HATE] shouldBe false
                    categories[ModerationCategory.HATE_THREATENING] shouldBe false
                    categories[ModerationCategory.ILLICIT] shouldBe false
                    categories[ModerationCategory.ILLICIT_VIOLENT] shouldBe false
                    categories[ModerationCategory.SELF_HARM_INTENT] shouldBe false
                    categories[ModerationCategory.SELF_HARM_INSTRUCTIONS] shouldBe false
                    categories[ModerationCategory.SELF_HARM] shouldBe false
                    categories[ModerationCategory.SEXUAL_MINORS] shouldBe false
                    categories[ModerationCategory.VIOLENCE] shouldBe true
                    categories[ModerationCategory.VIOLENCE_GRAPHIC] shouldBe true

                    categoryScores[ModerationCategory.HARASSMENT] shouldBe 0.8189693396524255
                    categoryScores[ModerationCategory.HARASSMENT_THREATENING] shouldBe
                        0.804985420696006
                    categoryScores[ModerationCategory.SEXUAL] shouldBe 1.573112165348997e-6
                    categoryScores[ModerationCategory.HATE] shouldBe 0.007562942636942845
                    categoryScores[ModerationCategory.HATE_THREATENING] shouldBe
                        0.004208854591835476
                    categoryScores[ModerationCategory.ILLICIT] shouldBe 0.030535955153511665
                    categoryScores[ModerationCategory.ILLICIT_VIOLENT] shouldBe 0.008925306722380033
                    categoryScores[ModerationCategory.SELF_HARM_INTENT] shouldBe
                        0.00023023930975076432
                    categoryScores[ModerationCategory.SELF_HARM_INSTRUCTIONS] shouldBe
                        0.0002293869201073356
                    categoryScores[ModerationCategory.SELF_HARM] shouldBe 0.012598046106750154
                    categoryScores[ModerationCategory.SEXUAL_MINORS] shouldBe 2.212566909570261e-8
                    categoryScores[ModerationCategory.VIOLENCE] shouldBe 0.9999992735124786
                    categoryScores[ModerationCategory.VIOLENCE_GRAPHIC] shouldBe 0.843064871157054

                    categoryAppliedInputTypes[ModerationCategory.HARASSMENT] shouldBe listOf(TEXT)
                    categoryAppliedInputTypes[ModerationCategory.HARASSMENT_THREATENING] shouldBe
                        listOf(
                            TEXT,
                        )
                    categoryAppliedInputTypes[ModerationCategory.SEXUAL] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                    categoryAppliedInputTypes[ModerationCategory.HATE] shouldBe listOf(TEXT)
                    categoryAppliedInputTypes[ModerationCategory.HATE_THREATENING] shouldBe
                        listOf(
                            TEXT,
                        )
                    categoryAppliedInputTypes[ModerationCategory.ILLICIT] shouldBe listOf(TEXT)
                    categoryAppliedInputTypes[ModerationCategory.ILLICIT_VIOLENT] shouldBe
                        listOf(
                            TEXT,
                        )
                    categoryAppliedInputTypes[ModerationCategory.SELF_HARM_INTENT] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                    categoryAppliedInputTypes[ModerationCategory.SELF_HARM_INSTRUCTIONS] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                    categoryAppliedInputTypes[ModerationCategory.SELF_HARM] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                    categoryAppliedInputTypes[ModerationCategory.SEXUAL_MINORS] shouldBe
                        listOf(TEXT)
                    categoryAppliedInputTypes[ModerationCategory.VIOLENCE] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                    categoryAppliedInputTypes[ModerationCategory.VIOLENCE_GRAPHIC] shouldBe
                        listOf(
                            TEXT,
                            IMAGE,
                        )
                }
            }
        }
    }
}
