package me.kpavlov.aimocks.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import com.openai.models.ChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MockOpenaiJavaTest {
    private static final MockOpenai MOCK_OPENAI = new MockOpenai();

    private static final Random RANDOM = new Random();

    private static final OpenAIClient CLIENT = OpenAIOkHttpClient.builder().apiKey("demo").baseUrl("http://127.0.0.1:" + MOCK_OPENAI.port() + "/v1").build();

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
            req.maxCompletionTokens(maxTokens);
            req.requestBodyContains("say 'Hello!'");
        }).responds(response -> {
            response.textContent("Hello");
            response.setFinishReason("stop");
        });

        final var params = ChatCompletionCreateParams.builder()
            .temperature(temperature)
            .maxCompletionTokens(maxTokens)
            .messages(
                List.of(ChatCompletionMessageParam.ofUser(
                    ChatCompletionUserMessageParam.builder()
                        .role(JsonValue.from("user"))
                        .content(ChatCompletionUserMessageParam.Content.ofText("Just say 'Hello!'")).build())))
            .model(ChatModel.GPT_4O_MINI)
            .build();

        final var result = CLIENT.chat().completions().create(params);

        assertThat(result.choices().get(0).message().content()).hasValue("Hello");
    }
}
