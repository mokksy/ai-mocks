package me.kpavlov.mokksy

import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.receiveNullable
import kotlinx.coroutines.runBlocking

public data class CapturedRequest<P>(
    val request: ApplicationRequest,
) {
    // todo: val body: P by lazy { runBlocking { request.call.receive... } }
    val bodyAsString: String? by lazy {
        runBlocking { request.call.receiveNullable<String>() }
    }
}
