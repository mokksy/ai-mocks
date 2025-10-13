package dev.mokksy.aimocks.a2a.adk;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.models.Gemini;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import dev.mokksy.aimocks.gemini.MockGemini;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SystemStubsExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdkTest {

    private static final MockGemini mockGemini = new MockGemini(0, true);

    private final String projectId = "987654321";
    private final String location = "galaxy-west2";
    private final String accessToken = "my-fake-access-token";
    private final String apiKey = "my-fake-api-key";

    @SystemStub
    private final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @BeforeAll
    void beforeAll() {
        environmentVariables.set("GOOGLE_GENAI_USE_VERTEXAI", "false");
        environmentVariables.set("GOOGLE_API_KEY", "test-key");
        environmentVariables.set("GOOGLE_GEMINI_BASE_URL", mockGemini.baseUrl());
        environmentVariables.set("GOOGLE_CLOUD_PROJECT", projectId);
        environmentVariables.set("GOOGLE_CLOUD_LOCATION", location);
    }

    @AfterAll
    void afterAll() {
        mockGemini.shutdown();
    }

    private BaseAgent initAgent() {
        final var credentials = GoogleCredentials.newBuilder().setAccessToken(
            AccessToken.newBuilder().setTokenValue(accessToken).build()
        ).build();
        final var client = Client.builder()
            .credentials(credentials)
            .apiKey(apiKey)
            .build();

        return LlmAgent.builder()
            .model(new Gemini("super-model-pro", client))
            .name("science-app")
            .description("Science teacher agent")
            .model("gemini-2.0-flash")
            .instruction("""
                You are a helpful science teacher that explains
                science concepts to kids and teenagers.
                """)
            .build();
    }

    /**
     * Simple agent test
     *
     * @see <a href="https://glaforge.dev/posts/2025/05/20/writing-java-ai-agents-with-adk-for-java-getting-started/">Write AI agents in Java — Agent Development Kit getting started guide</a>
     */
    @Test()
    void shouldCallAgent() {
        final var rootAgent = initAgent();

        InMemoryRunner runner = new InMemoryRunner(rootAgent);

        Session session = runner
            .sessionService()
            .createSession(runner.appName(), "student")
            .blockingGet();

        final var inputs = List.of(
            "What is a qbit? Please answer in a concise manner.",
            "quit"
        );

        final var modelResponse = "A **qubit** (short for \"quantum bit\") is the basic unit of information\n" +
            "in a quantum computer. Unlike a regular bit in your computer, which is\n" +
            "either a 0 or a 1, a qubit can be a 0, a 1, or *both at the same time*\n" +
            "thanks to something called **superposition**! This \"both at once\"\n" +
            "ability is what makes quantum computers super powerful for certain\n" +
            "kinds of problems.";

        mockGemini.generateContent(requestSpec -> {
            requestSpec.userMessageContains("What is a qbit?");
            requestSpec.project(projectId);
            requestSpec.location(location);
            requestSpec.path("/v1beta/models/gemini-2.0-flash:generateContent");
        }).responds(responseSpec -> {
            responseSpec.content(modelResponse);
        });

        final var receivedResponses = new ArrayList<String>();

        for (String userInput : inputs) {
            System.out.print("\nYou > " + userInput + "\n");
            if ("quit".equalsIgnoreCase(userInput)) {
                break;
            }

            Content userMsg = Content.fromParts(Part.fromText(userInput));
            Flowable<Event> events =
                runner.runAsync(session.userId(), session.id(), userMsg);

            System.out.print("\nAgent > ");
            events.blockingForEach(event -> {
                final var contentString = event.stringifyContent();
                receivedResponses.add(contentString);
                System.out.println(contentString);
            });
        }

        assertThat(receivedResponses).containsExactly(modelResponse);
    }
}
