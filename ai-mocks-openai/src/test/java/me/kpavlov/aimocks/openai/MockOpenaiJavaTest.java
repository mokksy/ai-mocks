package me.kpavlov.aimocks.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class MockOpenaiJavaTest {
  private final MockOpenai openai = new MockOpenai();

  private final Random random = new Random();
  private final OpenAIClient client =
    OpenAIOkHttpClient
      .builder()
      .apiKey("demo")
      .baseUrl("http://127.0.0.1:" + openai.port() + "/v1")
      .build();
  private double temperature;
  private long maxTokens;

  @BeforeEach
  void beforeEach() {
    temperature = random.nextDouble(0.0, 1.0);
    maxTokens = random.nextLong(100, 500);
  }

  @Test
  void shouldRespondToChatCompletion() {
    openai.completion(req -> {
      req.temperature(temperature);
      req.model("gpt-4o-mini");
      req.maxCompletionTokens(maxTokens);
      req.requestBodyContains("say 'Hello!'");
    }).responds(response -> {
      response.textContent("Hello");
      response.setFinishReason("stop");
    });

    ChatCompletionCreateParams params =
      ChatCompletionCreateParams
        .builder()
        .temperature(temperature)
        .maxCompletionTokens(maxTokens)
        .messages(
          List.of(
            ChatCompletionMessageParam.ofUser(
              ChatCompletionUserMessageParam
                .builder()
                .role(JsonValue.from("user"))
                .content(
                  ChatCompletionUserMessageParam.Content.ofText(
                    "Just say 'Hello!'"
                  )
                )
                .build()
            )
          )
        )
        .model(ChatModel.GPT_4O_MINI)
        .build();

    ChatCompletion result =
      client
        .chat()
        .completions()
        .create(params);

    assertThat(
      result
        .choices()
        .get(0)
        .message()
        .content()
    ).hasValue("Hello");
  }
}
