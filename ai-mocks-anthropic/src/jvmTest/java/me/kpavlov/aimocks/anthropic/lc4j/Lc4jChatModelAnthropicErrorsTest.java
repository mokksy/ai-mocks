package me.kpavlov.aimocks.anthropic.lc4j;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.internal.client.AnthropicHttpException;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import me.kpavlov.aimocks.anthropic.MockAnthropic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_HAIKU_20241022;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Lc4jChatModelAnthropicErrorsTest {

    private static final MockAnthropic MOCK = new MockAnthropic(0, true);

    public static final Duration TIMEOUT = Duration.ofSeconds(3);

    private static final ChatModel model = AnthropicChatModel.builder()
        .apiKey("dummy-key")
        .baseUrl(MOCK.baseUrl() + "/v1")
        .modelName(CLAUDE_3_5_HAIKU_20241022)
        .maxTokens(20)
        .timeout(TIMEOUT)
        .logRequests(true)
        .logResponses(true)
        .build();

    @AfterEach
    void afterEach() {
        MOCK.verifyNoUnmatchedRequests();
    }

    /**
     * See <a href="https://docs.anthropic.com/en/api/errors#http-errors">Anthropic HTTP errors</a>
     */
    @ParameterizedTest
    @CsvSource({
        "400, invalid_request_error",
        "401, authentication_error",
        "403, permission_error",
        "404, not_found_error",
        "413, request_too_large",
        "429, rate_limit_error",
        "500, api_error",
        "529, overloaded_error",
    })
    void shouldHandleErrorResponse(int httpStatusCode, String type) {
        final var question = "Respond with error " + httpStatusCode + ": " + type;
        final var errorMessage = "Error: " + type;

        // language=json
        final var responseBody =
            """
                {
                  "type": "error",
                  "error": {
                    "type": "%s",
                    "message": "%s"
                  }
                }
                """
                .formatted(type, errorMessage);

        MOCK.messages(req -> {
                req.systemMessageContains(type);
                req.userMessageContains(question);
            })
            .respondsError(res -> {
                res.setBody(responseBody);
                res.httpStatus(httpStatusCode);
            });

        // when-then
        final var chatRequest = ChatRequest.builder().messages(
            systemMessage("Let's test " + type),
            userMessage(question)
        ).build();

        assertThatExceptionOfType(AnthropicHttpException.class)
            .as("Handle Http status code: %s", httpStatusCode)
            // when
            .isThrownBy(() -> model.chat(chatRequest))
            .satisfies(ex -> {
                assertThat(ex.statusCode()).as("statusCode").isEqualTo(httpStatusCode);
                assertThat(ex.getMessage()).as("message").isEqualTo(responseBody);
            });
    }

    @Test
    void shouldHandleTimeout() {
        // given
        final var question = "Simulate timeout " + System.currentTimeMillis();
        MOCK.messages(req -> req.userMessageContains(question))
            .responds(res -> {
                res.delayMillis(TIMEOUT.plusMillis(200).toMillis());
                res.assistantContent("You should never see this");
            });

        // when-then
        final var chatRequest = ChatRequest.builder()
            .messages(userMessage(question))
            .build();

        assertThatExceptionOfType(RuntimeException.class)
            // when
            .isThrownBy(() -> model.chat(chatRequest))
            .satisfies(ex -> {
                    if (ex instanceof AnthropicHttpException ae) {
                        assertThat(ae.statusCode()).isBetween(500, 503);
                    } else {
                        assertThat(ex).hasMessageContaining("time");
                    }
                }
            );
    }
}
