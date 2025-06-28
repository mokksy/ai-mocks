package me.kpavlov.aimocks.gemini.genai

import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.genai.Client
import com.google.genai.types.HttpOptions
import me.kpavlov.aimocks.gemini.AbstractMockGeminiTest
import me.kpavlov.aimocks.gemini.gemini
import org.junit.jupiter.api.BeforeAll

internal abstract class AbstractGenaiTest : AbstractMockGeminiTest() {
    protected lateinit var client: Client

    @BeforeAll
    fun createChatClient() {
        client = Client.builder()
            .project(projectId)
            .location(locationId)
            .credentials(
                GoogleCredentials.create(
                    AccessToken.newBuilder().setTokenValue("dummy-token").build()
                )
            )
            .vertexAI(true)
            .httpOptions(HttpOptions.builder().baseUrl(gemini.baseUrl()).build())
            .build()

    }

//    protected fun prepareClientRequest(): ChatClient.ChatClientRequestSpec =
//        chatClient
//            .prompt()
//            .system("You are a helpful pirate")
//            .user("Just say 'Hello!'")
}
