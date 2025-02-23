package me.kpavlov.mokksy

import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

public data class CapturedRequest<P : Any>(
    val request: ApplicationRequest,
    private val type: KClass<P>,
) {
    val body: P by lazy { runBlocking { request.call.receive(type) } }
    val bodyAsString: String? by lazy {
        runBlocking { request.call.receiveNullable<String>() }
    }
}
