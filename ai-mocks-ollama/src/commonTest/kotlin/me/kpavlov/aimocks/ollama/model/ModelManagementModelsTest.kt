package me.kpavlov.aimocks.ollama.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * Tests for the serialization and deserialization of model management models.
 */
internal class ModelManagementModelsTest : AbstractSerializationTest() {
    @Test
    fun `Deserialize and Serialize ListModelsResponse`() {
        // language=json
        val payload =
            """
            {
              "models": [
                {
                  "name": "llama3.2:latest",
                  "modified": "2023-11-04T14:56:49Z",
                  "size": 4404659739,
                  "digest": "sha256:3d32fd1c3c0a7e9d9d79c9273d6d90159ca8f0c4f96c0b8a0887c761cbc7c1e4",
                  "details": {
                    "format": "gguf",
                    "family": "llama",
                    "families": ["llama", "llama3"],
                    "parameter_size": "8B",
                    "quantization_level": "Q4_0"
                  }
                },
                {
                  "name": "mistral:latest",
                  "modified": "2023-11-04T14:56:49Z",
                  "size": 4404659739,
                  "digest": "sha256:3d32fd1c3c0a7e9d9d79c9273d6d90159ca8f0c4f96c0b8a0887c761cbc7c1e4"
                }
              ]
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ListModelsResponse>(payload)
        model.models.size shouldBe 2
        model.models[0].name shouldBe "llama3.2:latest"
        model.models[0].modified shouldBe "2023-11-04T14:56:49Z"
        model.models[0].size shouldBe 4404659739
        model.models[0].digest shouldBe
            "sha256:3d32fd1c3c0a7e9d9d79c9273d6d90159ca8f0c4f96c0b8a0887c761cbc7c1e4"
        model.models[0].details?.format shouldBe "gguf"
        model.models[0].details?.family shouldBe "llama"
        model.models[0].details?.families shouldBe listOf("llama", "llama3")
        model.models[0].details?.parameterSize shouldBe "8B"
        model.models[0].details?.quantizationLevel shouldBe "Q4_0"

        model.models[1].name shouldBe "mistral:latest"
        model.models[1].details shouldBe null
    }

    @Test
    fun `Deserialize and Serialize ShowModelRequest`() {
        // language=json
        val payload =
            """
            {
              "name": "llama3.2:latest"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ShowModelRequest>(payload)
        model.name shouldBe "llama3.2:latest"
    }

    @Test
    fun `Deserialize and Serialize ShowModelResponse`() {
        // language=json
        val payload =
            """
            {
              "modelfile": "FROM llama3.2\nPARAMETER temperature 0.7\nPARAMETER top_p 0.9\nSYSTEM You are a helpful assistant.",
              "parameters": {
                "temperature": "0.7",
                "top_p": "0.9"
              },
              "template": "{{ .System }}\\n\\n{{ .Prompt }}",
              "system": "You are a helpful assistant.",
              "license": "Apache 2.0"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ShowModelResponse>(payload)
        model.modelfile shouldBe
            "FROM llama3.2\nPARAMETER temperature 0.7\nPARAMETER top_p 0.9\nSYSTEM You are a helpful assistant."
        model.parameters shouldBe mapOf("temperature" to "0.7", "top_p" to "0.9")
        model.template shouldBe "{{ .System }}\\n\\n{{ .Prompt }}"
        model.system shouldBe "You are a helpful assistant."
        model.license shouldBe "Apache 2.0"
    }

    @Test
    fun `Deserialize and Serialize CopyModelRequest`() {
        // language=json
        val payload =
            """
            {
              "source": "llama3.2:latest",
              "destination": "llama3.2:custom"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<CopyModelRequest>(payload)
        model.source shouldBe "llama3.2:latest"
        model.destination shouldBe "llama3.2:custom"
    }

    @Test
    fun `Deserialize and Serialize DeleteModelRequest`() {
        // language=json
        val payload =
            """
            {
              "name": "llama3.2:latest"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<DeleteModelRequest>(payload)
        model.name shouldBe "llama3.2:latest"
    }

    @Test
    fun `Deserialize and Serialize PullModelRequest`() {
        // language=json
        val payload =
            """
            {
              "name": "llama3.2:latest",
              "insecure": true,
              "stream": false
            }
            """.trimIndent()

        val model = deserializeAndSerialize<PullModelRequest>(payload)
        model.name shouldBe "llama3.2:latest"
        model.insecure shouldBe true
        model.stream shouldBe false
    }

    @Test
    fun `Deserialize and Serialize PullModelResponse`() {
        // language=json
        val payload =
            """
            {
              "status": "pulling manifest",
              "digest": "sha256:3d32fd1c3c0a7e9d9d79c9273d6d90159ca8f0c4f96c0b8a0887c761cbc7c1e4",
              "total": 4404659739,
              "completed": 1000000
            }
            """.trimIndent()

        val model = deserializeAndSerialize<PullModelResponse>(payload)
        model.status shouldBe "pulling manifest"
        model.digest shouldBe
            "sha256:3d32fd1c3c0a7e9d9d79c9273d6d90159ca8f0c4f96c0b8a0887c761cbc7c1e4"
        model.total shouldBe 4404659739
        model.completed shouldBe 1000000
    }

    @Test
    fun `Deserialize and Serialize CreateModelRequest`() {
        // language=json
        val payload =
            """
            {
              "name": "llama3.2:custom",
              "modelfile": "FROM llama3.2\nPARAMETER temperature 0.7\nPARAMETER top_p 0.9\nSYSTEM You are a helpful assistant.",
              "stream": false
            }
            """.trimIndent()

        val model = deserializeAndSerialize<CreateModelRequest>(payload)
        model.name shouldBe "llama3.2:custom"
        model.modelfile shouldBe
            "FROM llama3.2\nPARAMETER temperature 0.7\nPARAMETER top_p 0.9\nSYSTEM You are a helpful assistant."
        model.stream shouldBe false
    }

    @Test
    fun `Deserialize and Serialize ListRunningModelsResponse`() {
        // language=json
        val payload =
            """
            {
              "models": [
                {
                  "name": "llama3.2:latest",
                  "id": "01234567-89ab-cdef-0123-456789abcdef",
                  "created": "2023-11-04T14:56:49Z"
                },
                {
                  "name": "mistral:latest",
                  "id": "fedcba98-7654-3210-fedc-ba9876543210",
                  "created": "2023-11-04T14:56:49Z"
                }
              ]
            }
            """.trimIndent()

        val model = deserializeAndSerialize<ListRunningModelsResponse>(payload)
        model.models.size shouldBe 2
        model.models[0].name shouldBe "llama3.2:latest"
        model.models[0].id shouldBe "01234567-89ab-cdef-0123-456789abcdef"
        model.models[0].created shouldBe "2023-11-04T14:56:49Z"
        model.models[1].name shouldBe "mistral:latest"
        model.models[1].id shouldBe "fedcba98-7654-3210-fedc-ba9876543210"
        model.models[1].created shouldBe "2023-11-04T14:56:49Z"
    }

    @Test
    fun `Deserialize and Serialize VersionResponse`() {
        // language=json
        val payload =
            """
            {
              "version": "0.5.1"
            }
            """.trimIndent()

        val model = deserializeAndSerialize<VersionResponse>(payload)
        model.version shouldBe "0.5.1"
    }
}
