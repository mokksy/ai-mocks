package dev.mokksy.aimocks.a2a

import dev.mokksy.aimocks.a2a.model.AgentCard
import dev.mokksy.aimocks.a2a.model.create
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

internal class AgentCardIT : AbstractIT() {
    @Test
    suspend fun `Should get AgentCard`() {
        val agentCard =
            AgentCard.create {
                name = "test-agent"
                description = "test-agent-description"
                url = a2aServer.baseUrl()
                documentationUrl = "https://example.com/documentation"
                version = "0.0.1"
                provider {
                    organization = "Acme, Inc."
                    url = "https://example.com/organization"
                }
                capabilities {
                    streaming = true
                    pushNotifications = true
                    stateTransitionHistory = true
                }
                skills +=
                    skill {
                        id = "walk"
                        name = "Walk the walk"
                        description = "I can walk"
                        tags = listOf("move")
                    }
                skills +=
                    skill {
                        id = "talk"
                        name = "Talk the talk"
                        description = "I can talk"
                        tags = listOf("communicate")
                    }
            }

        a2aServer.agentCard() responds {
            delay = 1.milliseconds
            card = agentCard
        }

        val response =
            a2aClient
                .get("/.well-known/agent-card.json") {
                }.call
                .response
                .body<String>()

        val receivedCard = Json.decodeFromString<AgentCard>(response)
        receivedCard shouldBeEqual agentCard
    }
}
