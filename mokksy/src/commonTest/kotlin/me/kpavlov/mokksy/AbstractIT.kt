package me.kpavlov.mokksy

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.server.application.log
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

val mokksy: Mokksy =
    Mokksy(verbose = true) {
        it.log.info("Running Mokksy server with ${it.engine} engine")
    }

internal abstract class AbstractIT(
    clientSupplier: (Int) -> HttpClient = {
        createKtorClient(it)
    },
) {
    protected val client: HttpClient = clientSupplier(mokksy.port())

    protected val logger = KotlinLogging.logger(name = this::class.simpleName!!)

    /**
     * Represents a seed value is used for random number generation in tests.
     * Initialized to `-1` by default, it is updated before each test execution to a random value.
     * This ensures variability and uniqueness for random-based operations during every test run.
     */
    protected var seed: Int = -1

    @BeforeTest
    fun beforeEach() {
        seed = Random.nextInt(42, 100500)
    }

    @AfterTest
    fun afterEach() {
        mokksy.checkForUnmatchedRequests()
    }
}
