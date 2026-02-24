package dev.mokksy.test.utils

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Runs the given integration test block within a coroutine with a timeout.
 *
 * This function runs the provided suspend block in a coroutine with a fixed timeout of 15 seconds.
 * It is typically used to execute integration tests that need to be completed within a specified time limit.
 *
 * Use it as a last resort to run suspend function tests on non-JVM platforms.
 *
 * @param timeout The duration after which the test will time out. Defaults to 10 seconds.
 * @param block A suspend lambda function representing the integration test to be executed.
 *          The test logic should be placed inside this block.
 * @return Unit Nothing is returned from this function.
 */
public fun runIntegrationTest(
    timeout: Duration = 15.seconds,
    block: suspend () -> Unit,
) {
    runBlocking {
        withTimeout(timeout) {
            block()
        }
    }
}
