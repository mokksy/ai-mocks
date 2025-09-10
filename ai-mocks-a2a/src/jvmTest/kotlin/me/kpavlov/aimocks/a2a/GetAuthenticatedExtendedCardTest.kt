package me.kpavlov.aimocks.a2a

import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.test.runTest
import me.kpavlov.aimocks.a2a.model.AgentCard
import me.kpavlov.aimocks.a2a.model.AgentProvider
import me.kpavlov.aimocks.a2a.model.Data
import me.kpavlov.aimocks.a2a.model.GetAuthenticatedExtendedCardRequest
import me.kpavlov.aimocks.a2a.model.GetAuthenticatedExtendedCardResponse
import me.kpavlov.aimocks.a2a.model.agentCapabilities
import me.kpavlov.aimocks.a2a.model.agentCardSignature
import me.kpavlov.aimocks.a2a.model.agentSkill
import me.kpavlov.aimocks.a2a.model.getAuthenticatedExtendedCardRequest
import me.kpavlov.aimocks.a2a.model.invalidParamsError
import kotlin.test.Test

@Suppress("LongMethod")
internal class GetAuthenticatedExtendedCardTest : AbstractTest() {
    /**
     * https://a2a-protocol.org/latest/specification/#710-agentgetauthenticatedextendedcard
     */
    @Test
    fun `Should get authenticated extended agent card`() =
        runTest {
            val agentCard =
                AgentCard(
                    name = "Test Agent",
                    description = "Test Agent Description",
                    url = "https://example.com/agent",
                    provider =
                        AgentProvider(
                            organization = "Test Organization",
                            url = "https://example.com/organization",
                        ),
                    version = "1.0.0",
                    documentationUrl = "https://example.com/docs",
                    capabilities =
                        agentCapabilities {
                            streaming = true
                            pushNotifications = true
                        },

                    defaultInputModes = listOf("text", "voice"),
                    defaultOutputModes = listOf("text", "audio"),
                    skills =
                        listOf(
                            agentSkill {
                                id = "test-skill"
                                name = "Test Skill"
                                description = "A test skill for demonstration"
                                tags = listOf("test")
                            },
                        ),
                    signatures = listOf(
                        agentCardSignature {
                            header = Data.of("a" to "b", "foo" to 42)
                            protectedHeader = "e30"
                            signature = "sig1"
                        },
                        agentCardSignature {
                            protectedHeader = "e30"
                            signature = "sig2"
                        },
                    ),
                    supportsAuthenticatedExtendedCard = true,
                )

            a2aServer.getAuthenticatedExtendedCard() responds {
                id = 1
                result = agentCard
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            getAuthenticatedExtendedCardRequest {
                                id = "1"
                            }
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<GetAuthenticatedExtendedCardResponse>()

            val expectedReply =
                GetAuthenticatedExtendedCardResponse(
                    id = 1,
                    result = agentCard,
                )
            payload shouldBeEqualToComparingFields expectedReply
        }

    @Test
    fun `Should fail to get authenticated extended agent card`() =
        runTest {
            a2aServer.getAuthenticatedExtendedCard() responds {
                id = 1
                error =
                    invalidParamsError {
                        message = "Authenticated Extended Card not configured"
                    }
            }

            val response =
                a2aClient
                    .post("/") {
                        val jsonRpcRequest =
                            GetAuthenticatedExtendedCardRequest(
                                id = "1",
                            )
                        contentType(ContentType.Application.Json)
                        setBody(jsonRpcRequest)
                    }.call
                    .response

            response.status shouldBe HttpStatusCode.OK
            val payload = response.body<GetAuthenticatedExtendedCardResponse>()

            val expectedReply =
                GetAuthenticatedExtendedCardResponse(
                    id = 1,
                    error =
                        invalidParamsError {
                            message = "Authenticated Extended Card not configured"
                        },
                )
            payload shouldBeEqualToComparingFields expectedReply
        }
}
