package me.kpavlov.mokksy

import io.ktor.server.application.log
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.request.receiveNullable
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

public data class CapturedRequest<P : Any>(
    val request: ApplicationRequest,
    private val type: KClass<P>,
) {
    val body: P by lazy {
        runBlocking {
            try {
                request.call.receive(type)
            } catch (e: ContentTransformationException) {
                request.call.application.log
                    .debug("Failed to parse request body to {}", type.jvmName, e)
                throw e
            }
        }
    }
    val bodyAsString: String? by lazy {
        runBlocking { request.call.receiveNullable<String>() }
    }
}
