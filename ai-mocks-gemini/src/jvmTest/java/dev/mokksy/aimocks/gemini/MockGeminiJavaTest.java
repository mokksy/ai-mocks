package dev.mokksy.aimocks.gemini;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MockGeminiJavaTest {
    private static final MockGemini MOCK_GEMINI = new MockGemini();

    private static final Random RANDOM = new Random();

    private static final String projectId = "1234567890";
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

    @BeforeEach
    void beforeEach() {
        seed = RANDOM.nextInt(1000);
        temperature = RANDOM.nextDouble(0.0, 1.0);
        maxTokens = RANDOM.nextLong(100, 500);
    }

    @Test
    void shouldRespondToChatCompletion() {
        final var modelName = "gemini-x";
        final var userPrompt = "Hello, " + seed;
        MOCK_GEMINI.generateContent(req -> {
            req.project(projectId);
            req.location(locationId);
            req.model(modelName);
            req.betaApi();
            req.temperature(temperature);
            req.maxOutputTokens(maxTokens);
            req.userMessageContains(userPrompt);
            req.requestMatchesPredicate(it -> it.getContents().size() == 1);
        }).responds(response -> {
            response.assistantContent("Hey, " + seed + "!");
            response.finishReason("stop");
            response.delayMillis(42);
        });

        final var params = GenerateContentConfig.builder()
            .maxOutputTokens((int) maxTokens)
            .temperature((float) temperature)
            .systemInstruction(
                Content.builder().role("system")
                    .parts(Part.fromText("You are a helpful assistant")).build()
            )
            .build();

        final var result = CLIENT.models.generateContent(
            modelName,
            userPrompt,
            params
        );

        assertThat(result.text()).isEqualTo("Hey, " + seed + "!");
    }
}
