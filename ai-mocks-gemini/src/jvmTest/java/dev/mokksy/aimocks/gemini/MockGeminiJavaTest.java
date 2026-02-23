package dev.mokksy.aimocks.gemini;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.HttpOptions;
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

class MockGeminiJavaTest {
    private static final MockGemini MOCK_GEMINI = new MockGemini();

    private static final Random RANDOM = new Random();

    private static final String projectId = String.valueOf(Math.abs(RANDOM.nextLong()));
    private static final String locationId = "super-location";

    private static final Client CLIENT = Client.builder()
        .project(projectId)
        .location(locationId)
        .credentials(
            GoogleCredentials.create(
                AccessToken.newBuilder().setTokenValue("dummy-token").build()
            )
        )
        .vertexAI(true)
        .httpOptions(HttpOptions.builder().baseUrl(MOCK_GEMINI.baseUrl()).build())
        .build();

    private double temperature;
    private long maxTokens;
    private int seed;
    private double topP;
    private long topK;

    @BeforeEach
    void beforeEach() {
        seed = RANDOM.nextInt(1000);
        temperature = RANDOM.nextDouble(0.0, 1.0);
        maxTokens = RANDOM.nextLong(100, 500);
        topP = RANDOM.nextDouble(0.1, 1.0);
        topK = RANDOM.nextLong(1, 42);
    }

    @Test
    void shouldRespondToChatCompletion() {
        final var modelName = "gemini-x";
        final var userPrompt = "Hello, " + seed;
        final var systemPrompt = "You are a helpful assistant";
        MOCK_GEMINI.generateContent(req -> {
            req.project(projectId);
            req.location(locationId);
            req.model(modelName);
            req.betaApi();
            req.temperature(temperature);
            req.maxOutputTokens(maxTokens);
            req.topK(topK);
            req.topP(topP);
            req.seed(seed);
            req.systemMessageContains(systemPrompt);
            req.userMessageContains(userPrompt);
            req.requestMatchesPredicate(it ->
                it.getContents().size() == 1
                    && it.getSystemInstruction() != null
                    && it.getSystemInstruction().getParts().stream()
                    .anyMatch(p -> systemPrompt.equals(p.getText()))
            );
        }).responds(response -> {
            response.assistantContent("Hey, " + seed + "!");
            response.finishReason("stop");
            response.delayMillis(42);
        });

        final var result = CLIENT.models.generateContent(
            modelName,
            userPrompt,
            generateContentConfig(systemPrompt)
        );

        assertThat(result.text()).isEqualTo("Hey, " + seed + "!");
    }

    @ParameterizedTest
    @MethodSource("requestMutators")
    void shouldMissResponseWhenRequestDoesNotMatch(Consumer<GenerateContentConfig.Builder> mutator) {
        final var modelName = "gemini-x";
        final var userPrompt = "Hello, " + seed;
        final var systemPrompt = "You are a helpful assistant";
        MOCK_GEMINI.generateContent(req -> {
            req.project(projectId);
            req.location(locationId);
            req.model(modelName);
            req.betaApi();
            req.temperature(temperature);
            req.maxOutputTokens(maxTokens);
            req.topK(topK);
            req.topP(topP);
            req.seed(seed);
            req.systemMessageContains(systemPrompt);
            req.userMessageContains(userPrompt);
        }).responds(response -> {
            response.assistantContent("Hey, " + seed + "!");
            response.finishReason("stop");
        });

        final var configBuilder = generateContentConfigBuilder(systemPrompt);
        mutator.accept(configBuilder);

        assertThatThrownBy(() -> CLIENT.models.generateContent(modelName, userPrompt, configBuilder.build()))
            .isInstanceOfSatisfying(
                ClientException.class,
                e -> assertThat(e.code()).isEqualTo(404)
            );
    }

    private GenerateContentConfig generateContentConfig(String systemPrompt) {
        return generateContentConfigBuilder(systemPrompt).build();
    }

    private GenerateContentConfig.Builder generateContentConfigBuilder(String systemPrompt) {
        return GenerateContentConfig.builder()
            .maxOutputTokens((int) maxTokens)
            .temperature((float) temperature)
            .topK((float) topK)
            .topP((float) topP)
            .seed(seed)
            .systemInstruction(
                Content.builder().role("system")
                    .parts(Part.fromText(systemPrompt)).build()
            );
    }

    private static Stream<Arguments> requestMutators() {
        return Stream.of(
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.topK(42.0f + 1)),
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.topP(0.05f)),       // below the [0.1, 1.0) floor — valid and never collides
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.temperature(2.0f)), // above the [0.0, 1.0) ceiling — valid for Vertex AI Gemini
            Arguments.of((Consumer<GenerateContentConfig.Builder>) builder ->
                builder.maxOutputTokens(500 + 1))
        );
    }
}
