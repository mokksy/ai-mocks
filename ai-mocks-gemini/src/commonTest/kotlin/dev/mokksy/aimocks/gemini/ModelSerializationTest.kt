package dev.mokksy.aimocks.gemini

import dev.mokksy.test.utils.deserializeAndSerialize
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ModelSerializationTest {
    @Test
    fun `should serialize and deserialize SafetyCategory enum values`() {
        // given
        val json = """{"category":"HARM_CATEGORY_HATE_SPEECH","threshold":"BLOCK_LOW_AND_ABOVE"}"""

        // when
        val safetySetting = deserializeAndSerialize<SafetySetting>(json)

        // then
        assertSoftly(safetySetting) {
            category shouldBe SafetyCategory.HARM_CATEGORY_HATE_SPEECH
            threshold shouldBe HarmBlockThreshold.BLOCK_LOW_AND_ABOVE
        }
    }

    @Test
    fun `should serialize and deserialize all SafetyCategory values`() {
        // when & then
        SafetyCategory.entries.forEach { category ->
            val safetySetting = SafetySetting(
                category = category,
                threshold = HarmBlockThreshold.BLOCK_NONE,
            )
            val json = kotlinx.serialization.json.Json.encodeToString(SafetySetting.serializer(), safetySetting)
            val deserialized = kotlinx.serialization.json.Json.decodeFromString(SafetySetting.serializer(), json)
            deserialized.category shouldBe category
        }
    }

    @Test
    fun `should serialize and deserialize all HarmBlockThreshold values`() {
        // when & then
        HarmBlockThreshold.entries.forEach { threshold ->
            val safetySetting = SafetySetting(
                category = SafetyCategory.HARM_CATEGORY_UNSPECIFIED,
                threshold = threshold,
            )
            val json = kotlinx.serialization.json.Json.encodeToString(SafetySetting.serializer(), safetySetting)
            val deserialized = kotlinx.serialization.json.Json.decodeFromString(SafetySetting.serializer(), json)
            deserialized.threshold shouldBe threshold
        }
    }

    @Test
    fun `should serialize and deserialize SafetyRating with HarmProbability`() {
        // given
        val json = """{"category":"HARM_CATEGORY_DANGEROUS_CONTENT","probability":"HIGH"}"""

        // when
        val safetyRating = deserializeAndSerialize<SafetyRating>(json)

        // then
        assertSoftly(safetyRating) {
            category shouldBe SafetyCategory.HARM_CATEGORY_DANGEROUS_CONTENT
            probability shouldBe HarmProbability.HIGH
        }
    }

    @Test
    fun `should serialize and deserialize all HarmProbability values`() {
        // when & then
        HarmProbability.entries.forEach { probability ->
            val safetyRating = SafetyRating(
                category = SafetyCategory.HARM_CATEGORY_UNSPECIFIED,
                probability = probability,
            )
            val json = kotlinx.serialization.json.Json.encodeToString(SafetyRating.serializer(), safetyRating)
            val deserialized = kotlinx.serialization.json.Json.decodeFromString(SafetyRating.serializer(), json)
            deserialized.probability shouldBe probability
        }
    }

    @Test
    fun `should serialize and deserialize UsageMetadata with all fields`() {
        // given
        val json = """
            {
                "promptTokenCount": 100,
                "cachedContentTokenCount": 50,
                "candidatesTokenCount": 200,
                "toolUsePromptTokenCount": 30,
                "thoughtsTokenCount": 10,
                "totalTokenCount": 390
            }
        """.trimIndent()

        // when
        val metadata = deserializeAndSerialize<UsageMetadata>(json)

        // then
        assertSoftly(metadata) {
            promptTokenCount shouldBe 100
            cachedContentTokenCount shouldBe 50
            candidatesTokenCount shouldBe 200
            toolUsePromptTokenCount shouldBe 30
            thoughtsTokenCount shouldBe 10
            totalTokenCount shouldBe 390
        }
    }

    @Test
    fun `should serialize and deserialize UsageMetadata with minimal fields`() {
        // given
        val json = """{"promptTokenCount": 50, "totalTokenCount": 100}"""

        // when
        val metadata = deserializeAndSerialize<UsageMetadata>(json)

        // then
        assertSoftly(metadata) {
            promptTokenCount shouldBe 50
            totalTokenCount shouldBe 100
            cachedContentTokenCount shouldBe null
            candidatesTokenCount shouldBe null
        }
    }

    @Test
    fun `should serialize and deserialize ModalityTokenCount`() {
        // given
        val json = """{"modality":"TEXT","tokenCount":42}"""

        // when
        val modalityTokenCount = deserializeAndSerialize<ModalityTokenCount>(json)

        // then
        assertSoftly(modalityTokenCount) {
            modality shouldBe Modality.TEXT
            tokenCount shouldBe 42
        }
    }

    @Test
    fun `should serialize and deserialize all Modality values`() {
        // when & then
        Modality.entries.forEach { modality ->
            val modalityTokenCount = ModalityTokenCount(
                modality = modality,
                tokenCount = 100,
            )
            val json = kotlinx.serialization.json.Json.encodeToString(ModalityTokenCount.serializer(), modalityTokenCount)
            val deserialized = kotlinx.serialization.json.Json.decodeFromString(ModalityTokenCount.serializer(), json)
            deserialized.modality shouldBe modality
        }
    }

    @Test
    fun `should serialize and deserialize UsageMetadata with token details`() {
        // given
        val json = """
            {
                "promptTokenCount": 100,
                "totalTokenCount": 200,
                "promptTokensDetails": [
                    {"modality": "TEXT", "tokenCount": 80},
                    {"modality": "IMAGE", "tokenCount": 20}
                ],
                "candidatesTokensDetails": [
                    {"modality": "TEXT", "tokenCount": 100}
                ]
            }
        """.trimIndent()

        // when
        val metadata = deserializeAndSerialize<UsageMetadata>(json)

        // then
        assertSoftly(metadata) {
            promptTokenCount shouldBe 100
            totalTokenCount shouldBe 200
            promptTokensDetails.shouldNotBeNull {
                size shouldBe 2
                this[0].modality shouldBe Modality.TEXT
                this[0].tokenCount shouldBe 80
                this[1].modality shouldBe Modality.IMAGE
                this[1].tokenCount shouldBe 20
            }
            candidatesTokensDetails.shouldNotBeNull {
                size shouldBe 1
                this[0].modality shouldBe Modality.TEXT
                this[0].tokenCount shouldBe 100
            }
        }
    }

    @Test
    fun `should serialize and deserialize PromptFeedback with block reason`() {
        // given
        val json = """
            {
                "blockReason": "SAFETY",
                "safetyRatings": [
                    {"category": "HARM_CATEGORY_HARASSMENT", "probability": "HIGH"}
                ]
            }
        """.trimIndent()

        // when
        val feedback = deserializeAndSerialize<PromptFeedback>(json)

        // then
        assertSoftly(feedback) {
            blockReason shouldBe BlockReason.SAFETY
            safetyRatings.shouldNotBeNull {
                size shouldBe 1
                this[0].category shouldBe SafetyCategory.HARM_CATEGORY_HARASSMENT
                this[0].probability shouldBe HarmProbability.HIGH
            }
        }
    }

    @Test
    fun `should serialize and deserialize GenerationConfig with all fields`() {
        // given
        val json = """
            {
                "temperature": 0.9,
                "topP": 0.8,
                "topK": 40.0,
                "candidateCount": 1,
                "maxOutputTokens": 2048,
                "stopSequences": ["STOP", "END"],
                "responseMimeType": "application/json",
                "seed": 12345,
                "presencePenalty": 0.5,
                "frequencyPenalty": 0.3,
                "responseLogprobs": true,
                "logprobs": 5,
                "enableEnhancedCivicAnswers": true
            }
        """.trimIndent()

        // when
        val config = deserializeAndSerialize<GenerationConfig>(json)

        // then
        assertSoftly(config) {
            temperature shouldBe 0.9
            topP shouldBe 0.8
            topK shouldBe 40.0f
            candidateCount shouldBe 1
            maxOutputTokens shouldBe 2048
            stopSequences shouldBe listOf("STOP", "END")
            responseMimeType shouldBe "application/json"
            seed shouldBe 12345
            presencePenalty shouldBe 0.5
            frequencyPenalty shouldBe 0.3
            responseLogprobs shouldBe true
            logprobs shouldBe 5
            enableEnhancedCivicAnswers shouldBe true
        }
    }

    @Test
    fun `should serialize and deserialize GenerationConfig with response modalities`() {
        // given
        val json = """
            {
                "temperature": 0.7,
                "responseModalities": ["TEXT", "IMAGE"]
            }
        """.trimIndent()

        // when
        val config = deserializeAndSerialize<GenerationConfig>(json)

        // then
        assertSoftly(config) {
            temperature shouldBe 0.7
            responseModalities.shouldNotBeNull {
                size shouldBe 2
                this[0] shouldBe Modality.TEXT
                this[1] shouldBe Modality.IMAGE
            }
        }
    }

    @Test
    fun `should serialize and deserialize all MediaResolution values`() {
        // when & then
        MediaResolution.entries.forEach { resolution ->
            val config = GenerationConfig(mediaResolution = resolution)
            val json = kotlinx.serialization.json.Json.encodeToString(GenerationConfig.serializer(), config)
            val deserialized = kotlinx.serialization.json.Json.decodeFromString(GenerationConfig.serializer(), json)
            deserialized.mediaResolution shouldBe resolution
        }
    }

    @Test
    fun `should serialize and deserialize SpeechConfig`() {
        // given
        val json = """{"voice": "en-US-Studio-M"}"""

        // when
        val config = deserializeAndSerialize<SpeechConfig>(json)

        // then
        config.voice shouldBe "en-US-Studio-M"
    }

    @Test
    fun `should serialize and deserialize ThinkingConfig`() {
        // given
        val json = """{"enabled": true}"""

        // when
        val config = deserializeAndSerialize<ThinkingConfig>(json)

        // then
        config.enabled shouldBe true
    }

    @Test
    fun `should serialize and deserialize GenerationConfig with speech and thinking config`() {
        // given
        val json = """
            {
                "temperature": 0.8,
                "speechConfig": {"voice": "en-US-Neural2-A"},
                "thinkingConfig": {"enabled": true},
                "mediaResolution": "HIGH"
            }
        """.trimIndent()

        // when
        val config = deserializeAndSerialize<GenerationConfig>(json)

        // then
        assertSoftly(config) {
            temperature shouldBe 0.8
            speechConfig.shouldNotBeNull {
                voice shouldBe "en-US-Neural2-A"
            }
            thinkingConfig.shouldNotBeNull {
                enabled shouldBe true
            }
            mediaResolution shouldBe MediaResolution.HIGH
        }
    }

    @Test
    fun `should serialize and deserialize Schema`() {
        // given
        val json = """{"type": "object"}"""

        // when
        val schema = deserializeAndSerialize<Schema>(json)

        // then
        schema.type shouldBe "object"
    }

    @Test
    fun `should serialize and deserialize FunctionDeclaration`() {
        // given
        val json = """
            {
                "name": "get_weather",
                "description": "Get the weather for a location"
            }
        """.trimIndent()

        // when
        val functionDecl = deserializeAndSerialize<FunctionDeclaration>(json)

        // then
        assertSoftly(functionDecl) {
            name shouldBe "get_weather"
            description shouldBe "Get the weather for a location"
        }
    }

    @Test
    fun `should serialize and deserialize Tool with function declarations`() {
        // given
        val json = """
            {
                "function_declarations": [
                    {
                        "name": "get_weather",
                        "description": "Get weather"
                    }
                ]
            }
        """.trimIndent()

        // when
        val tool = deserializeAndSerialize<Tool>(json)

        // then
        assertSoftly(tool) {
            functionDeclarations.size shouldBe 1
            functionDeclarations[0].name shouldBe "get_weather"
            functionDeclarations[0].description shouldBe "Get weather"
        }
    }

    @Test
    fun `should serialize and deserialize Candidate with all fields`() {
        // given
        val json = """
            {
                "content": {
                    "parts": [{"text": "Response text"}],
                    "role": "model"
                },
                "finishReason": "stop",
                "safetyRatings": [
                    {"category": "HARM_CATEGORY_HATE_SPEECH", "probability": "NEGLIGIBLE"}
                ]
            }
        """.trimIndent()

        // when
        val candidate = deserializeAndSerialize<Candidate>(json)

        // then
        assertSoftly(candidate) {
            content.parts.size shouldBe 1
            content.parts[0].text shouldBe "Response text"
            content.role shouldBe "model"
            finishReason shouldBe "stop"
            safetyRatings.shouldNotBeNull {
                size shouldBe 1
                this[0].category shouldBe SafetyCategory.HARM_CATEGORY_HATE_SPEECH
                this[0].probability shouldBe HarmProbability.NEGLIGIBLE
            }
        }
    }

    @Test
    fun `should serialize and deserialize GenerateContentResponse with all metadata`() {
        // given
        val json = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [{"text": "Hello!"}]
                        },
                        "finishReason": "stop"
                    }
                ],
                "promptFeedback": {
                    "safetyRatings": [
                        {"category": "HARM_CATEGORY_HARASSMENT", "probability": "LOW"}
                    ]
                },
                "usageMetadata": {
                    "promptTokenCount": 10,
                    "candidatesTokenCount": 5,
                    "totalTokenCount": 15
                },
                "modelVersion": "gemini-1.5-pro",
                "responseId": "resp-123"
            }
        """.trimIndent()

        // when
        val response = deserializeAndSerialize<GenerateContentResponse>(json)

        // then
        assertSoftly(response) {
            candidates.size shouldBe 1
            candidates[0].content.parts[0].text shouldBe "Hello!"
            candidates[0].finishReason shouldBe "stop"
            promptFeedback.shouldNotBeNull {
                safetyRatings?.size shouldBe 1
            }
            usageMetadata.shouldNotBeNull {
                promptTokenCount shouldBe 10
                candidatesTokenCount shouldBe 5
                totalTokenCount shouldBe 15
            }
            modelVersion shouldBe "gemini-1.5-pro"
            responseId shouldBe "resp-123"
        }
    }

    @Test
    fun `should serialize and deserialize Part with text`() {
        // given
        val json = """{"text": "Sample text"}"""

        // when
        val part = deserializeAndSerialize<Part>(json)

        // then
        part.text shouldBe "Sample text"
    }

    @Test
    fun `should serialize and deserialize Content with parts and role`() {
        // given
        val json = """
            {
                "parts": [
                    {"text": "First part"},
                    {"text": "Second part"}
                ],
                "role": "user"
            }
        """.trimIndent()

        // when
        val content = deserializeAndSerialize<Content>(json)

        // then
        assertSoftly(content) {
            parts.size shouldBe 2
            parts[0].text shouldBe "First part"
            parts[1].text shouldBe "Second part"
            role shouldBe "user"
        }
    }

    @Test
    fun `should serialize and deserialize GenerateContentRequest with system instruction and tools`() {
        // given
        val json = """
            {
                "contents": [
                    {
                        "parts": [{"text": "Hello"}],
                        "role": "user"
                    }
                ],
                "model": "gemini-pro",
                "systemInstruction": {
                    "parts": [{"text": "You are helpful"}]
                },
                "tools": [
                    {
                        "function_declarations": [
                            {"name": "search", "description": "Search the web"}
                        ]
                    }
                ]
            }
        """.trimIndent()

        // when
        val request = deserializeAndSerialize<GenerateContentRequest>(json)

        // then
        assertSoftly(request) {
            contents.size shouldBe 1
            contents[0].parts[0].text shouldBe "Hello"
            model shouldBe "gemini-pro"
            systemInstruction.shouldNotBeNull {
                parts[0].text shouldBe "You are helpful"
            }
            tools.shouldNotBeNull {
                size shouldBe 1
                this[0].functionDeclarations[0].name shouldBe "search"
            }
        }
    }
}