package me.kpavlov.mokksy

import io.kotest.matchers.equals.beEqual
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
            mokksy.get(
                configuration =
                    StubConfiguration(
                        removeAfterMatch = true,
                        verbose = true,
                    ),
            ) {
                path = beEqual(uri)
            } respondsWith {
                body = "🇪🇪 Tere!"
            }
            // First request should succeed
            client.get(uri).status shouldBe HttpStatusCode.OK
            // Next request should fail as stub is self-removed
            client.get(uri).status shouldBe HttpStatusCode.NotFound
        }
}
