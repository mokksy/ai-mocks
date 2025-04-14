package me.kpavlov.aimocks.a2a

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.AfterEach

internal abstract class AbstractTest {
    protected val logger = KotlinLogging.logger(name = javaClass.canonicalName!!)
    protected val a2aServer = MockAgentServer(verbose = true)

    protected val a2aClient = createA2AClient(a2aServer.port())

    @AfterEach
    fun afterEach() {
        a2aServer.verifyNoUnmatchedRequests()
    }
}
