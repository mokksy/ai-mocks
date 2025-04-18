package me.kpavlov.a2a.client

import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.create
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
                    authentication {
                        schemes = listOf("none", "bearer")
                        credentials = "test-token"
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
                        }
                    skills +=
                        skill {
                            id = "talk"
                            name = "Talk the talk"
                        }
                }

            a2aServer.agentCard() responds {
                delay = 1.milliseconds
                card = agentCard
            }

            val receivedCard =
                client.getAgentCard()

            receivedCard shouldBeEqual agentCard
        }
}
