package dev.mokksy.aimocks.gemini

import com.google.api.gax.core.NoCredentialsProvider
import com.google.auth.ApiKeyCredentials
import com.google.cloud.vertexai.Transport
import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.LlmUtilityServiceClient
import com.google.cloud.vertexai.api.LlmUtilityServiceSettings
import com.google.cloud.vertexai.api.PredictionServiceClient
import com.google.cloud.vertexai.api.PredictionServiceSettings
import com.google.cloud.vertexai.api.stub.LlmUtilityServiceStubSettings
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.toJavaDuration

internal fun createTestVertexAI(
    endpoint: String,
    projectId: String,
    location: String,
    timeout: Duration,
): VertexAI {
    try {
        val channelProvider =
            LlmUtilityServiceStubSettings
                .defaultHttpJsonTransportProviderBuilder()
                .setEndpoint(endpoint)
                .build()

        val newHttpJsonBuilder = LlmUtilityServiceStubSettings.newHttpJsonBuilder()
        newHttpJsonBuilder.unaryMethodSettingsBuilders().forEach { builder ->
            builder.setSimpleTimeoutNoRetriesDuration(timeout.toJavaDuration())
        }

        val llmUtilityServiceStubSettings =
            newHttpJsonBuilder
                .setEndpoint(endpoint)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .setTransportChannelProvider(channelProvider)
                .build()

        val llmUtilityServiceClient =
            LlmUtilityServiceClient.create(
                LlmUtilityServiceSettings.create(llmUtilityServiceStubSettings),
            )

        val predictionServiceSettingsBuilder =
            PredictionServiceSettings
                .newHttpJsonBuilder()
                .setEndpoint(endpoint)
                .setCredentialsProvider(NoCredentialsProvider.create())
                .applyToAllUnaryMethods { updater ->
                    @Suppress("ForbiddenVoid")
                    updater.setSimpleTimeoutNoRetriesDuration(timeout.toJavaDuration()) as? Void?
                }

        val predictionServiceSettings = predictionServiceSettingsBuilder.build()
        val predictionClient = PredictionServiceClient.create(predictionServiceSettings)

        return VertexAI
            .Builder()
            .setTransport(Transport.REST)
            .setProjectId(projectId)
            .setLocation(location)
            .setLlmClientSupplier { llmUtilityServiceClient }
            .setPredictionClientSupplier { predictionClient }
            .setCredentials(ApiKeyCredentials.create("dummy-key"))
            .build()
    } catch (e: IOException) {
        throw RuntimeException(e)
    }
}
