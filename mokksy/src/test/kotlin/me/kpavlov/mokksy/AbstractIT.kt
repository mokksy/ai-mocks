package me.kpavlov.mokksy

import io.ktor.client.HttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.random.Random

internal abstract class AbstractIT(
    clientSupplier: (Int) -> HttpClient = {
        createKtorClient(it)
    },
) {
    protected val mokksy: MokksyServer =
        MokksyServer(verbose = true) {
            println("Running server with ${it.engine} engine")
        }

    protected val client: HttpClient = clientSupplier(mokksy.port())

    /**
     * Represents a seed value that is used for random number generation in tests.
     * Initialized to `-1` by default, it is updated before each test execution to a random value.
     * This ensures variability and uniqueness for random-based operations during every test run.
     */
    protected var seed: Int = -1

    @BeforeEach
    fun beforeEach() {
        seed = Random.nextInt(42, 100500)
    }

    @AfterEach
    fun afterEach() {
        mokksy.checkForUnmatchedRequests()
    }
}
