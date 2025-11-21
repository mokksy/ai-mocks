package dev.mokksy.mokksy.request

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import kotlin.reflect.KClass

/**
 * The default priority value assigned to a stub when no explicit priority is specified.
 *
 * This constant is used in the context of mapping and comparing inbound [RequestSpecification]s
 * (such as stubs or routes) to determine their evaluation order. Lower numerical values generally indicate
 * higher priority.
 *
 * By default, a stub with [DEFAULT_STUB_PRIORITY] has the lowest possible priority,
 * as it is equal to the maximum value of an `Int`.
 */
public const val DEFAULT_STUB_PRIORITY: Int = Int.MAX_VALUE

/**
 * Represents a specification for matching incoming HTTP requests based on defined criteria,
 * such as HTTP method, request path, and request body.
 *
 * This class is used to define the criteria against which incoming requests are tested.
 * It provides functionality for checking whether a given request satisfies the specified
 * conditions and supports prioritization for defining matching order.
 *
 * @param P type of the request payload
 * @property method Matcher for the HTTP method of the request. If null, the method is not validated.
 * @property path Matcher for the request path. If null, the path is not validated.
 * @property headers List of matchers for Ktor Headers object. All matchers must pass for a match to succeed.
 * @property body List of matchers for the request body as a [P]. All matchers must pass for a match to succeed.
 * @property bodyString List of matchers for the request body as a String.
 *                      All matchers must pass for a match to succeed.
 * @property priority The priority value used for comparing different specifications.
 * Lower values indicate higher priority. Default value is [DEFAULT_STUB_PRIORITY]
 */
@Suppress("LongParameterList")
public open class RequestSpecification<P : Any>(
    public val method: Matcher<HttpMethod>? = null,
    public val path: Matcher<String>? = null,
    public val headers: List<Matcher<Headers>> = listOf(),
    public val body: List<Matcher<P?>> = listOf(),
    public val bodyString: List<Matcher<String?>> = listOf(),
    public val priority: Int? = DEFAULT_STUB_PRIORITY,
    private val requestType: KClass<P>,
) {
    internal fun priority(): Int = priority ?: DEFAULT_STUB_PRIORITY

    public suspend fun matches(request: ApplicationRequest): Result<Boolean> =
        runCatching {
            (method == null || method.test(request.httpMethod).passed()) &&
                (path == null || path.test(request.path()).passed()) &&
                matchHeaders(headers, request) &&
                matchBody(body, request) &&
                matchBodyString(bodyString, request)
        }

    protected suspend fun matchBody(
        matchers: List<Matcher<P?>>,
        request: ApplicationRequest,
    ): Boolean {
        if (matchers.isEmpty()) return true
        val body: P?
        return try {
            body = request.call.receive(requestType)
            matchers.all {
                it
                    .test(body)
                    .passed()
            }
        } catch (e: ContentTransformationException) {
            @Suppress("TooGenericExceptionCaught")
            val bodyText =
                try {
                    request.call.receiveText()
                } catch (ex: Exception) {
                    "Unable to read body: ${ex.message}"
                }
            request.call.application.log
                .debug(
                    "Request payload can not be transformed to {}: {}. Request body: {}. Cause: {}",
                    requestType,
                    e.message,
                    bodyText,
                    e.cause?.message ?: "No cause available",
                    e,
                )
            false
        } catch (e: BadRequestException) {
            request.call.application.log
                .debug(
                    "Bad request: ${e.message}. Request body: ${request.call.receiveText()}",
                    e,
                )
            false
        }
    }

    /**
     * Matches the body (string) content of an HTTP request against a provided list of matchers.
     *
     * @param matchers A list of matchers used to evaluate the HTTP request body as a string.
     *                      All matchers must pass for the method to return true.
     * @param request The HTTP request to be evaluated.
     * @return True if all matchers successfully match the request body, false otherwise.
     */
    protected suspend fun matchBodyString(
        matchers: List<Matcher<String>>,
        request: ApplicationRequest,
    ): Boolean {
        if (matchers.isEmpty()) return true
        val bodyString = request.call.receive(type = String::class)
        return matchers.all {
            it
                .test(bodyString)
                .passed()
        }
    }

    /**
     * Matches the headers of an HTTP request against a provided list of matchers.
     *
     * @param headersMatchers A list of matchers used to evaluate the HTTP request headers.
     *                        All matchers must pass for the method to return true.
     * @param request The HTTP request whose headers will be evaluated.
     * @return True if all matchers successfully match the request headers, false otherwise.
     */
    protected fun matchHeaders(
        headersMatchers: List<Matcher<Headers>>,
        request: ApplicationRequest,
    ): Boolean =
        headersMatchers.all {
            it
                .test(request.headers)
                .passed()
        }

    internal fun toLogString(): String =
        buildList(3) {
            if (method != null) {
                add("method: $method")
            }
            if (path != null) add("path: $path")
            if (headers.isNotEmpty()) add("headers: $headers")
            if (body.isNotEmpty()) add("body: $body")
            if (bodyString.isNotEmpty()) add("bodyString: $bodyString")
        }.joinToString(", ")
}

public open class RequestSpecificationBuilder<P : Any>(
    protected val requestType: KClass<P>,
) {
    protected var method: Matcher<HttpMethod>? = null
    public var path: Matcher<String>? = null
    public var headers: MutableList<Matcher<Headers>> = mutableListOf()
    public val body: MutableList<Matcher<P?>> = mutableListOf()
    public val bodyString: MutableList<Matcher<String?>> = mutableListOf()
    public var priority: Int? = DEFAULT_STUB_PRIORITY

    public fun method(matcher: Matcher<HttpMethod>): RequestSpecificationBuilder<P> {
        this.method = matcher
        return this
    }

    public fun path(matcher: Matcher<String>): RequestSpecificationBuilder<P> {
        this.path = matcher
        return this
    }

    public fun path(pathString: String): RequestSpecificationBuilder<P> {
        this.path = pathEqual(pathString)
        return this
    }

    public fun bodyContains(vararg strings: String): RequestSpecificationBuilder<P> {
        strings.forEach { this.bodyString += contain(it) }
        return this
    }

    /**
     * Adds a predicate to match against the request body.
     *
     * The specified predicate will be used to evaluate whether the request body satisfies the defined condition.
     *
     * @param description The predicate's description. Returned as `toString()` value
     * @param predicate The predicate used to evaluate the request body. It defines the condition
     *                  that the request body must satisfy.
     * @return The same instance of [RequestSpecificationBuilder] with the predicate applied
     *         for further customization.
     */
    public fun bodyMatchesPredicate(
        description: String? = null,
        predicate: (P?) -> Boolean,
    ): RequestSpecificationBuilder<P> {
        this.body +=
            predicateMatcher(
                description = description,
                predicate = predicate,
            )
        return this
    }

    /**
     * Adds multiple predicates to match against the request body.
     * Each predicate will be applied to evaluate whether the request body satisfies the specified conditions.
     *
     * @param predicate A variable number of predicates to evaluate the request body.
     *                  All predicates are added as matchers for the request body.
     * @return The same instance of [RequestSpecificationBuilder] with the predicates applied
     *         for further customization.
     */
    public fun bodyMatchesPredicates(
        vararg predicate: (P?) -> Boolean,
    ): RequestSpecificationBuilder<P> {
        predicate.forEach { bodyMatchesPredicate(predicate = it) }
        return this
    }

    public fun containsHeader(
        headerName: String,
        headerValue: String,
    ): RequestSpecificationBuilder<P> {
        headers +=
            dev.mokksy.mokksy.request
                .containsHeader(headerName, headerValue)
        return this
    }

    public fun bodyString(matcher: Matcher<String?>): RequestSpecificationBuilder<P> {
        this.bodyString += matcher
        return this
    }

    public fun priority(value: Int): RequestSpecificationBuilder<P> {
        this.priority = value
        return this
    }

    internal fun build(): RequestSpecification<P> =
        RequestSpecification(
            method = method,
            path = path,
            headers = headers,
            requestType = requestType,
            body = body,
            bodyString = bodyString,
            priority = priority ?: DEFAULT_STUB_PRIORITY,
        )
}
