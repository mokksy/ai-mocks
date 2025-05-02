package me.kpavlov.a2a.client

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.request.header
import me.kpavlov.aimocks.a2a.MockAgentServer
import org.junit.jupiter.api.AfterEach
import java.util.UUID

internal abstract class AbstractTest {
    protected val logger = KotlinLogging.logger(name = javaClass.canonicalName!!)
    protected val a2aServer = MockAgentServer(verbose = true)

    protected val client =
        A2AClientFactory.create(
            baseUrl = a2aServer.baseUrl(),
            defaultRequestConfigurer = {
                header("X-Test-Header", "test-header-value")
            },
            requestConfigurer = {
                header("X-Request-ID", "${UUID.randomUUID()}")
            },
        )

    @AfterEach
    fun afterEach() {
        a2aServer.verifyNoUnmatchedRequests()
    }
}
