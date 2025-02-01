package me.kpavlov.mokksy

import io.ktor.client.HttpClient
import org.junit.jupiter.api.AfterEach

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

    @AfterEach
    fun afterEach() {
        mokksy.checkForUnmatchedRequests()
    }
}
