package dev.mokksy.mokksy

import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class RemoveAfterUseIT : AbstractIT() {
    @Test
    fun `Should remove Stub after match`() =
        runTest {
            val uri = "/remove-after-match"
            mokksy
                .get(
                    configuration =
                        StubConfiguration(
                            removeAfterMatch = true,
                            verbose = true,
                        ),
                ) {
                    path(uri)
                }.respondsWith(String::class) {
                    body = "🇪🇪 Tere!"
                }
            // The first request should succeed
            client.get(uri).status shouldBe HttpStatusCode.OK
            // The next request should fail as stub is self-removed
            client.get(uri).status shouldBe HttpStatusCode.NotFound
        }
}
