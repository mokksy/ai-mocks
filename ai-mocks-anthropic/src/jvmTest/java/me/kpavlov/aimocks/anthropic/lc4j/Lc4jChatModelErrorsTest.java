package me.kpavlov.aimocks.anthropic.lc4j;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.internal.client.AnthropicHttpException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import io.ktor.http.HttpStatusCode;
import me.kpavlov.aimocks.anthropic.MockAnthropic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.InterruptedIOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_HAIKU_20241022;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Lc4jChatModelErrorsTest {

    private static final MockAnthropic MOCK = new MockAnthropic(0, true);

    public static final Duration TIMEOUT = Duration.ofMillis(300);
    private static final ChatLanguageModel model = AnthropicChatModel.builder()
        .apiKey("dummy-key")
        .baseUrl(MOCK.baseUrl() + "/v1")
        .modelName(CLAUDE_3_5_HAIKU_20241022)
        .maxTokens(20)
        .timeout(TIMEOUT)
        .logRequests(true)
        .logResponses(true)
        .build();

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

        MOCK.messages(req -> req.userMessageContains(question))
            .respondsError(res -> {
                res.setBody(responseBody);
                res.setHttpStatus(HttpStatusCode.Companion.fromValue(httpStatusCode));
            });

        // when-then
        assertThatExceptionOfType(RuntimeException.class)
            // when
            .isThrownBy(() -> model.chat(
                ChatRequest.builder().messages(userMessage(question)).build()))
            .satisfies(ex -> {
                final var cause = ex.getCause();
                assertThat(cause).isInstanceOf(AnthropicHttpException.class);
                final var anthropicHttpException = (AnthropicHttpException) cause;
                assertThat(anthropicHttpException.statusCode()).as("statusCode").isEqualTo(httpStatusCode);
            });
    }

    @Test
    void shouldHandleTimeout() {
        // given
        final var question = "Simulate timeout";
        MOCK.messages(req -> req.userMessageContains(question))
            .respondsError(res -> {
                res.delayMillis(TIMEOUT.plusMillis(100).toMillis());
                res.setHttpStatus(HttpStatusCode.Companion.getNoContent());
            });

        // when-then
        assertThatExceptionOfType(RuntimeException.class)
            // when
            .isThrownBy(() -> model.chat(
                ChatRequest.builder().messages(userMessage(question)).build()))
            .satisfies(ex -> assertThat(ex.getCause()).hasCauseInstanceOf(InterruptedIOException.class));
    }
}
