package dev.mokksy.aimocks.a2a

import io.github.oshai.kotlinlogging.KotlinLogging

@Suppress("AbstractClassCanBeConcreteClass")
internal abstract class AbstractIT {
    protected val logger = KotlinLogging.logger(name = javaClass.canonicalName!!)
    protected val a2aServer = MockAgentServer(verbose = true)

    protected val a2aClient = createA2AClient(url = a2aServer.baseUrl())

}
