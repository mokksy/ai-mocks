package me.kpavlov.mokksy

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.kotest.matchers.equals.beEqual
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class StubPriorityIT : AbstractIT() {
    @Test
    fun `Should consider stub priority (desc order)`() =
        runTest {
            val path = "/stub-priority-$seed"
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 1 // higher priority
                }.respondsWith(String::class) {
                    body = "Expected response"
                }

            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 2 // lower priority
                }.respondsWith(String::class) {
                    body = "Unexpected response"
                }

            // when
            val result =
                client.get(path)
            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.bodyAsText()).isEqualTo("Expected response")
        }

    @Test
    fun `Should consider stub priority (asc order)`() =
        runTest {
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 2 // lower priority
                }.respondsWith(String::class) {
                    body = "Unexpected response"
                }

            val path = "/stub-priority-$seed"
            mokksy
                .get {
                    this.path = beEqual(path)
                    this.priority = 1 // higher priority
                }.respondsWith(String::class) {
                    body = "Expected response"
                }

            // when
            val result =
                client.get(path)
            // then
            assertThat(result.status).isEqualTo(HttpStatusCode.OK)
            assertThat(result.bodyAsText()).isEqualTo("Expected response")
        }
}
