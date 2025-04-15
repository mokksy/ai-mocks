package me.kpavlov.aimocks.a2a

import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.kpavlov.aimocks.a2a.model.GetTaskRequest
import me.kpavlov.aimocks.core.AbstractBuildingStep
import me.kpavlov.aimocks.core.AbstractMockLlm
import me.kpavlov.mokksy.ServerConfiguration

public open class MockAgentServer(
    port: Int = 0,
    verbose: Boolean = false,
) : AbstractMockLlm(
        port = port,
        configuration =
            ServerConfiguration(
                verbose = verbose,
            ) { config ->
                config.json(
                    Json { ignoreUnknownKeys = true },
                )
            },
    ) {
    /**
     * Configures a behavior for handling
     * ["Agent Card"](https://google.github.io/A2A/#/documentation?id=agent-card) mock server requests.
     * This method simulates interactions with an endpoint that retrieves agent card data.
     *
     * > Remote Agents that support A2A are required to publish an Agent Card in JSON format describing
     * > the agent’s capabilities/skills and authentication mechanism.
     * > Clients use the Agent Card information to identify the best agent that can perform
     * > a task and leverage A2A to communicate with that remote agent.
     *
     * @param name An optional identifier for the configured request.
     * @return An instance of `AbstractBuildingStep` allowing further configuration of the response
     *         for agent card requests using `AgentCardResponseSpecification`.
     */
    public fun agentCard(
        name: String? = null,
    ): AbstractBuildingStep<Nothing, AgentCardResponseSpecification> {
        val requestStep =
            mokksy.get<Nothing>(
                name = name,
                requestType = Nothing::class,
            ) {
                path("/.well-known/agent.json")
            }

        return AgentCardBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }

    /**
     * Configures the behavior of the mocking server
     * to handle ["Send a Task"](https://google.github.io/A2A/#/documentation?id=send-a-task) requests.
     *
     * This method simulates the behavior of the A2A's
     * ["Send a Task"](https://google.github.io/A2A/#/documentation?id=send-a-task) endpoint.
     * It allows defining how the server should respond to a "send task" JsonRPC request by chaining
     * a response configuration using the `responds` method of the returned `SendTaskBuildingStep`.
     *
     * @return An instance of `SendTaskBuildingStep` to define the response behavior for "send task" requests.
     */
    public fun sendTask(): SendTaskBuildingStep = SendTaskBuildingStep(mokksy)

    /**
     * Configures the behavior of the mocking server to handle
     * ["Get a Task"](https://google.github.io/A2A/#/documentation?id=get-a-task) requests.
     *
     * This method defines a mock server behavior by simulating the A2A's
     * ["Get a Task"](https://google.github.io/A2A/#/documentation?id=get-a-task) endpoint. It creates
     * a request specification that listens for the "tasks/get" JsonRPC method and includes specific configurations
     * for response behaviors that can be chained using the returned `GetTaskBuildingStep` instance.
     *
     * @param name An optional identifier for the configured request. This can be used to distinguish between
     *             multiple mocked behaviors for "Get a Task" requests.
     * @return An instance of `GetTaskBuildingStep` that allows for further configuration of the mock
     *         response behavior for "Get a Task" requests.
     */
    public fun getTask(name: String? = null): GetTaskBuildingStep {
        val requestStep =
            mokksy
                .post(name = name, requestType = GetTaskRequest::class) {
                    this.path("/")
                    this.bodyMatchesPredicate {
                        it?.method == "tasks/get"
                    }
                }

        return GetTaskBuildingStep(
            buildingStep = requestStep,
            mokksy = mokksy,
        )
    }
}
