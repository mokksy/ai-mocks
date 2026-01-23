package dev.mokksy.aimocks.a2a

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.AfterEach

@Suppress("AbstractClassCanBeConcreteClass")
internal abstract class AbstractTest {
    protected val logger = KotlinLogging.logger(name = javaClass.canonicalName!!)
    protected val a2aServer = MockAgentServer(verbose = true)

    protected val a2aClient = createA2AClient(url = a2aServer.baseUrl())

    @AfterEach
    fun afterEach() {
        a2aServer.verifyNoUnmatchedRequests()
    }
}
