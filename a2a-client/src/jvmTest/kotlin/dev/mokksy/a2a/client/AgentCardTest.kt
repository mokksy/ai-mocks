package dev.mokksy.a2a.client

import dev.mokksy.aimocks.a2a.model.AgentCard
import dev.mokksy.aimocks.a2a.model.create
import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

internal class AgentCardTest : AbstractTest() {
    @Test
    fun `Should get AgentCard`() =
        runTest {
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
                            description = "Can Walk"
                            tags = listOf("movement")
                        }
                    skills +=
                        skill {
                            id = "talk"
                            name = "Talk the talk"
                            description = "Can Talk"
                            tags = listOf("communication")
                        }
                }

            a2aServer.agentCard(name = agentCard.name) responds {
                delay = 1.milliseconds
                card = agentCard
            }

            val receivedCard =
                client.getAgentCard()

            receivedCard shouldBeEqual agentCard
        }
}
