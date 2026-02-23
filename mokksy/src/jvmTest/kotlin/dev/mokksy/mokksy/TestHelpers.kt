package dev.mokksy.mokksy

import dev.mokksy.mokksy.request.RequestSpecification
import dev.mokksy.mokksy.response.AbstractResponseDefinition
import dev.mokksy.mokksy.response.ResponseDefinitionSupplier
import io.ktor.http.ContentType
import io.ktor.server.application.ApplicationCall
import kotlin.reflect.KClass

internal inline fun <reified T : Any> okResponseSupplier(): ResponseDefinitionSupplier<T> =
    { _ ->
        object : AbstractResponseDefinition<T>(
            contentType = ContentType.Any,
            httpStatusCode = 200,
        ) {
            override suspend fun writeResponse(
                call: ApplicationCall,
                verbose: Boolean,
            ) {
                // no-op for tests
            }
        }
    }

@Suppress("LongParameterList")
internal inline fun <P : Any, reified T : Any> createStub(
    name: String? = null,
    priority: Int? = null,
    removeAfterMatch: Boolean = false,
    requestType: KClass<P>,
    path: String? = null,
    noinline responseSupplier: ResponseDefinitionSupplier<T>? = null,
): Stub<P, T> {
    val spec =
        RequestSpecification(
            requestType = requestType,
            priority = priority,
            path =
                path?.let {
                    dev.mokksy.mokksy.request
                        .pathEqual(it)
                },
        )
    return Stub(
        configuration = StubConfiguration(name = name, removeAfterMatch = removeAfterMatch),
        requestSpecification = spec,
        responseDefinitionSupplier = responseSupplier ?: okResponseSupplier<T>(),
    )
}
