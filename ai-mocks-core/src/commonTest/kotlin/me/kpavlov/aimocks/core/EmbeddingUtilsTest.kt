package me.kpavlov.aimocks.core

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.floats.shouldBeLessThan
import io.kotest.matchers.shouldBe
import me.kpavlov.aimocks.core.EmbeddingUtils.calculateMagnitude
import me.kpavlov.aimocks.core.EmbeddingUtils.cosineSimilarity
import kotlin.math.abs
import kotlin.test.Test

internal class EmbeddingUtilsTest {

    @Test
    fun `generateEmbedding should create a vector of the specified dimension`() {
        // Given
        val input = "Hello, world!"
        val dimensions = 512

        // When
        val embedding = EmbeddingUtils.generateEmbedding(input, dimensions)

        // Then
        embedding shouldHaveSize dimensions
    }

    @Test
    fun `generateEmbedding should create a normalized vector with approximate unit length`() {
        // Given
        val input = "This is a test string for embedding generation"

        // When
        val embedding = EmbeddingUtils.generateEmbedding(input)

        // Then
        val magnitude = calculateMagnitude(embedding.toFloatArray())

        // The magnitude should be approximately 1.0 (allowing for small floating-point errors)
        abs(magnitude - 1.0f) shouldBeLessThan 0.01f
    }

    @Test
    fun `generateEmbedding should be deterministic for the same input`() {
        // Given
        val input = "Deterministic test"

        // When
        val embedding1 = EmbeddingUtils.generateEmbedding(input, 100)
        val embedding2 = EmbeddingUtils.generateEmbedding(input, 100)

        // Then
        embedding1 shouldBe embedding2
    }

    @Test
    fun `generateEmbedding should produce different vectors for different inputs`() {
        // Given
        val input1 = "First input line"
        val input2 = "Second input string"

        // When
        val embedding1 = EmbeddingUtils.generateEmbedding(input1, 100).toFloatArray()
        val embedding2 = EmbeddingUtils.generateEmbedding(input2, 100).toFloatArray()

        // Then
        // Calculate cosine similarity - should be less than 1.0 for different inputs
        val similarity = cosineSimilarity(embedding1, embedding2)
        similarity shouldBeLessThan 0.99f
    }

    @Test
    fun `generateEmbedding should handle empty input`() {
        // Given
        val input = ""
        val dimensions = 10

        // When
        val embedding = EmbeddingUtils.generateEmbedding(input, dimensions)

        // Then
        embedding shouldHaveSize dimensions
        embedding.all { it == 0.0f } shouldBe true
    }

    @Test
    fun `generateEmbeddings should create embeddings for multiple inputs`() {
        // Given
        val inputs = listOf("First string", "Second string", "Third string")
        val dimensions = 50

        // When
        val embeddings = EmbeddingUtils.generateEmbeddings(inputs, dimensions)

        // Then
        embeddings shouldHaveSize inputs.size
        embeddings.forEach { it shouldHaveSize dimensions }

        // Each embedding should be normalized
        embeddings.forEach {
            val magnitude = calculateMagnitude(it.toFloatArray())
            abs(magnitude - 1.0f) shouldBeLessThan 0.01f
        }
    }


}
