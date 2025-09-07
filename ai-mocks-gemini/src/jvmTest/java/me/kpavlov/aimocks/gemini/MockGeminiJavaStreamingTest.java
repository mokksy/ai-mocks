package me.kpavlov.aimocks.gemini;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.errors.ClientException;
import com.google.genai.types.*;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Java version of StreamingChatCompletionGenaiTest.kt
 * Some examples:
 * - <a href="https://github.com/google-gemini/cookbook/blob/main/quickstarts/rest/Streaming_REST.ipynb">Streaming_REST.ipynb</a>
 */
class MockGeminiJavaStreamingTest {
    private static final MockGemini MOCK = new MockGemini(true);
    private static final Random RANDOM = new Random();

    private static final String PROJECT_ID = "1234567890";
    private static final String LOCATION_ID = "us-central1";

    private static final Client CLIENT = Client.builder()
        .project(PROJECT_ID)
        .location(LOCATION_ID)
        .credentials(
            GoogleCredentials.create(
                AccessToken.newBuilder().setTokenValue("dummy-token").build()
            )
        )
        .vertexAI(true)
        .httpOptions(HttpOptions.builder().baseUrl(MOCK.baseUrl()).build())
        .build();

    private double temperatureValue;
    private int seedValue;
    private double topPValue;
    private long topKValue;
    private long maxCompletionTokensValue;
    private String modelName;

    @BeforeEach
    void beforeEach() {
        String[] models = {
            "gemini-2.0-flash",
            "gemini-2.0-flash-lite",
            "gemini-2.5-flash-preview-04-17",
            "gemini-2.5-pro-preview-03-25"
        };
        modelName = models[RANDOM.nextInt(models.length)];
        seedValue = RANDOM.nextInt(1, 100500);
        topPValue = RANDOM.nextDouble(0.1, 1.0);
        topKValue = RANDOM.nextLong(1, 42);
        temperatureValue = RANDOM.nextDouble(0.0, 1.0);
        maxCompletionTokensValue = RANDOM.nextLong(100, 500);
    }

    @Test
    void shouldRespondWithStreamToGenerateContentStream() {
        // Configure the mock server to respond with a stream
        final var systemMessage = "You are a helpful pirate." + seedValue;
        MOCK.generateContentStream(req -> {
            req.temperature(temperatureValue);
            req.apiVersion("v1beta1");
            req.location(LOCATION_ID);
            req.maxOutputTokens(maxCompletionTokensValue);
            req.model(modelName);
            req.project(PROJECT_ID);
            req.seed(seedValue);
            req.systemMessageContains(systemMessage);
            req.topK(topKValue);
            req.topP(topPValue);
            req.userMessageContains("Just say 'Hello!'");
        }).respondsStream(response -> {
            response.stream(
                Stream.of("Ahoy", " there,", " matey!", " Hello!")
            );
            response.delayMillis(60);
        });

        // Create request configuration
        GenerateContentConfig config = generateContentConfig(systemMessage);

        // Call the streaming API
        try (
            ResponseStream<GenerateContentResponse> responseStream = CLIENT.models.generateContentStream(
                modelName,
                "Just say 'Hello!'",
                config
            )) {

            final var result = new StringBuffer();
            // Collect and verify the response
            responseStream
                .forEach(
                    response -> {
                        if (response.text() != null) {
                            result.append(response.text());
                        }
                    });

            assertThat(result.toString()).isEqualTo("Ahoy there, matey! Hello!");

        }
    }

    @ParameterizedTest
    @MethodSource("requestMutators")
    void shouldMissResponseWhenRequestDoesNotMatch(Consumer<GenerateContentConfig.Builder> mutator) {
        // Configure the mock server to respond with a stream
        MOCK.generateContentStream(req -> {
            req.apiVersion("v1beta1");
            req.location(LOCATION_ID);
            req.maxOutputTokens(maxCompletionTokensValue);
            req.model(modelName);
            req.project(PROJECT_ID);
            req.seed(seedValue);
            req.systemMessageContains("You are a helpful pirate");
            req.temperature(temperatureValue);
            req.topK(topKValue);
            req.topP(topPValue);
            req.userMessageContains("Just say 'Hello!'");
        }).respondsStream(response -> {
            response.stream(
                Stream.of("Ahoy", " there,", " matey!", " Hello!")
            );
            response.delayMillis(60);
        });

        // Create request configuration with mutation
        GenerateContentConfig.Builder configBuilder = generateContentConfigBuilder("You are a helpful pirate");
        mutator.accept(configBuilder);
        GenerateContentConfig config = configBuilder.build();

        // Verify that the request throws ClientException with 404 code
        assertThatThrownBy(() -> {
            try (var ignored = CLIENT.models.generateContentStream(
                modelName,
                "Just say 'Hello!'",
                config
            )) {
                fail("No stream should be returned");
            }
        }).isInstanceOf(ClientException.class)
            .satisfies(exception -> {
                ClientException clientException = (ClientException) exception;
                assertThat(clientException.code()).isEqualTo(404);
            });
    }

    private GenerateContentConfig generateContentConfig(String systemPrompt) {
        return generateContentConfigBuilder(systemPrompt).build();
    }

    private GenerateContentConfig.Builder generateContentConfigBuilder(String systemPrompt) {
        return GenerateContentConfig.builder()
            .seed(seedValue)
            .maxOutputTokens((int) maxCompletionTokensValue)
            .temperature((float) temperatureValue)
            .topK((float) topKValue)
            .topP((float) topPValue)
            .systemInstruction(
                Content.builder()
                    .role("system")
                    .parts(Part.fromText(systemPrompt))
                    .build()
            );
    }

    private static Stream<Arguments> requestMutators() {
        return Stream.of(
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.topK(42.0f + 1)),
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.topP(0.8f + 1)),
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.temperature(0.5f / 2.0f)),
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.maxOutputTokens(200 + 1))
        );
    }
}
