package me.kpavlov.aimocks.core

import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utility functions for working with embeddings.
 */
@Suppress("MagicNumber")
public object EmbeddingUtils {

    /**
     * Generates a simple embedding vector from a string.
     *
     * This is a deterministic function that converts a string into a vector of floating-point numbers.
     * The vector is normalized so that its L2 norm (Euclidean length) is approximately 1.0.
     *
     * Note: This is a simple implementation for testing purposes only and does not produce
     * semantically meaningful embeddings like real language models would.
     *
     * @param input The input string to generate embeddings for
     * @param dimensions The number of dimensions in the output vector (default: 1536)
     * @return A list of floating-point numbers representing the embedding vector
     */
    public fun generateEmbedding(input: String, dimensions: Int = 1536): List<Float> {
        if (input.isEmpty()) return List(dimensions) { 0.0f }

        val hash = input.hashCode()
        val charSum = input.sumOf { it.code }

        var norm = 0.0f
        val result = FloatArray(dimensions) { i ->
            val pos = i.toFloat() / dimensions
            // Reduced input range to sin() to avoid periodicity collapse
            val angle = (hash * 0.001f) + (charSum * 0.001f) + pos
            val value = sin(angle * 6.2831f) // sin(2πx), keeps values in [-1, 1]
            norm += value * value
            value
        }
        norm = sqrt(norm)
        result.forEachIndexed { index, value ->
            result[index] = (value / norm)
        }

        return result.toList()
    }

    /**
     * Generates embedding vectors for a list of strings.
     *
     * @param inputs The list of input strings to generate embeddings for
     * @param dimensions The number of dimensions in each output vector (default: 1536)
     * @return A list of embedding vectors, one for each input string
     */
    public fun generateEmbeddings(inputs: List<String>, dimensions: Int = 1536): List<List<Float>> {
        return inputs.map { generateEmbedding(it, dimensions) }
    }

    /**
     * Calculates the magnitude (L2 norm) of a vector.
     */
    public fun calculateMagnitude(vector: FloatArray): Float {
        // Calculate sum of squares manually to avoid type conversion issues
        var sumOfSquares = 0.0f
        for (value in vector) {
            sumOfSquares += value * value
        }
        return sqrt(sumOfSquares)
    }

    /**
     * Calculates the cosine similarity between two vectors.
     */
    public fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Vectors must have the same dimension" }

        // Calculate dot product manually to avoid type conversion issues
        var dotProduct = 0.0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
        }

        val magnitudeA = calculateMagnitude(a)
        val magnitudeB = calculateMagnitude(b)

        return dotProduct / (magnitudeA * magnitudeB)
    }
}
