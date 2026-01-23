package dev.mokksy.aimocks.core

import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utility functions for working with embeddings.
 */
@Suppress("MagicNumber")
public object EmbeddingUtils {
    /**
     * Generates a deterministic, normalized embedding vector from a string.
     *
     * Converts the input string into a floating-point vector of the specified dimension,
     * using a repeatable mathematical transformation. The resulting vector
     * has an L2 norm of approximately 1.0. For empty input, returns a zero vector.
     *
     * Intended for testing purposes only; does not produce semantically meaningful embeddings.
     *
     * @param input The string to convert into an embedding vector.
     * @param dimensions The number of dimensions for the output vector (default is 1536).
     * @return A list of floats representing the normalized embedding vector.
     */
    public fun generateEmbedding(
        input: String,
        dimensions: Int = 1536,
    ): List<Float> {
        if (input.isEmpty()) return List(dimensions) { 0.0f }

        val hash = input.hashCode()
        val charSum = input.sumOf { it.code }

        var norm = 0.0f
        val result =
            FloatArray(dimensions) { i ->
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
     * Generates deterministic embedding vectors for a list of input strings.
     *
     * Each input string is converted into a normalized float vector
     * of the specified dimension using a deterministic algorithm.
     *
     * @param inputs The strings to generate embeddings for.
     * @param dimensions The number of dimensions for each embedding vector (default is 1536).
     * @return A list of embedding vectors corresponding to the input strings.
     */
    public fun generateEmbeddings(
        inputs: List<String>,
        dimensions: Int = 1536,
    ): List<List<Float>> = inputs.map { generateEmbedding(it, dimensions) }

    /**
     * Computes the L2 norm (Euclidean magnitude) of a float array vector.
     *
     * @param vector The input vector.
     * @return The L2 norm of the vector.
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
     * Computes the cosine similarity between two float vectors of equal dimension.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @return The cosine similarity value in the range [-1, 1].
     * @throws IllegalArgumentException if the vectors have different dimensions.
     */
    public fun cosineSimilarity(
        a: FloatArray,
        b: FloatArray,
    ): Float {
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
