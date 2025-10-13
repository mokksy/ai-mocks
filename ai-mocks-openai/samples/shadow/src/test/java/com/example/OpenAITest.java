package com.example;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.errors.OpenAIInvalidDataException;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;
import dev.mokksy.aimocks.openai.MockOpenai;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OpenAITest {

    private static final MockOpenai MOCK_OPENAI = new MockOpenai();

    private static final Random RANDOM = new Random();

    private static final OpenAIClient CLIENT = OpenAIOkHttpClient.builder()
        .apiKey("demo")
        .baseUrl(MOCK_OPENAI.baseUrl())
        .build();

    private double temperature;
    private long maxTokens;

    @BeforeEach
    void beforeEach() {
        temperature = RANDOM.nextDouble(0.0, 1.0);
        maxTokens = RANDOM.nextLong(100, 500);
    }

    @Test
    void shouldRespondToChatCompletion() {
        MOCK_OPENAI.completion(req -> {
            req.temperature(temperature);
            req.model("gpt-4o-mini");
            req.maxTokens(maxTokens);
            req.requestBodyContains("say 'Hey!'");
        }).responds(response -> {
            response.assistantContent("Hey!");
            response.finishReason("stop");
            response.delayMillis(42);
        });

        final ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
            .temperature(temperature)
            .maxCompletionTokens(maxTokens)
            .messages(
                List.of(ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                        .role(JsonValue.from("user"))
                        .content("Just say 'Hey!'").build())))
            .model(ChatModel.GPT_4O_MINI)
            .build();

        final var result = CLIENT.chat().completions().create(params);

        assertThat(result.choices().get(0).message().content()).hasValue("Hey!");
    }

    @Test
    void shouldRespondToChatCompletionWithError() {
        MOCK_OPENAI.completion(req -> {
            req.temperature(temperature);
            req.maxTokens(maxTokens);
        }).respondsError(response -> {
            response.setBody("Ahh, ohh!");
            response.setHttpStatusCode(500);
        });

        final ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
            .temperature(temperature)
            .maxCompletionTokens(maxTokens)
            .messages(
                List.of(ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                        .role(JsonValue.from("user"))
                        .content("Just say 'Hello!'").build())))
            .model(ChatModel.GPT_4O_MINI)
            .build();

        assertThatExceptionOfType(OpenAIInvalidDataException.class)
            .isThrownBy(() -> CLIENT.chat().completions().create(params));
    }
}
