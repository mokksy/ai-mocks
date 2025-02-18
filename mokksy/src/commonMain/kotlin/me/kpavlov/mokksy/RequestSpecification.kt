package me.kpavlov.mokksy

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive

/**
 * The default priority value assigned to a stub when no explicit priority is specified.
 *
 * This constant is used in the context of mapping and comparing inbound request specifications
 * (such as stubs or routes) to determine their evaluation order. Lower numerical values generally indicate
 *  * higher priority.
 *
 * By default, a stub with `DEFAULT_STUB_PRIORITY` has the lowest possible priority,
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
 * @property method Matcher for the HTTP method of the request. If null, the method is not validated.
 * @property path Matcher for the request path. If null, the path is not validated.
 * @property headers List of matchers for Ktor [Headers] object. All matchers must pass for a match to succeed.
 * @property body List of matchers for the request body as a string. All matchers must pass for a match to succeed.
 * @property priority The priority value used for comparing different specifications.
 * Lower values indicate higher priority. Default value is [DEFAULT_STUB_PRIORITY]
 */
public open class RequestSpecification(
    public val method: Matcher<HttpMethod>? = null,
    public val path: Matcher<String>? = null,
    public val headers: List<Matcher<Headers>> = listOf(),
    public val body: List<Matcher<String>> = listOf(),
    public val priority: Int? = DEFAULT_STUB_PRIORITY,
) {
    internal fun priority(): Int = priority ?: DEFAULT_STUB_PRIORITY

    public suspend fun matches(request: ApplicationRequest): Boolean =
        (method == null || method.test(request.httpMethod).passed()) &&
            (path == null || path.test(request.path()).passed()) &&
            matchHeaders(headers, request) &&
            matchBodyString(body, request)

    /**
     * Matches the body content of an HTTP request against a provided list of matchers.
     *
     * @param bodyMatchers A list of matchers used to evaluate the HTTP request body as a string.
     *                      All matchers must pass for the method to return true.
     * @param request The HTTP request to be evaluated.
     * @return True if all matchers successfully match the request body, false otherwise.
     */
    protected suspend fun matchBodyString(
        bodyMatchers: List<Matcher<String>>,
        request: ApplicationRequest,
    ): Boolean {
        if (bodyMatchers.isEmpty()) return true
        val bodyString = request.call.receive(type = String::class)
        return bodyMatchers.all {
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

    internal fun toLogString(): String = "method: $method, path: $path, body: $body"
}

public open class RequestSpecificationBuilder<B : RequestSpecificationBuilder<B>> {
    protected var method: Matcher<HttpMethod>? = null
    public var path: Matcher<String>? = null
    public var headers: MutableList<Matcher<Headers>> = mutableListOf()
    public val body: MutableList<Matcher<String>> = mutableListOf()
    public var priority: Int? = DEFAULT_STUB_PRIORITY

    public fun method(matcher: Matcher<HttpMethod>): RequestSpecificationBuilder<B> {
        this.method = matcher
        return this
    }

    public fun path(matcher: Matcher<String>): RequestSpecificationBuilder<B> {
        this.path = matcher
        return this
    }

    public fun bodyContains(vararg strings: String): RequestSpecificationBuilder<B> {
        strings.forEach { this.body += contain(it) }
        return this
    }

    public fun containsHeader(
        headerName: String,
        headerValue: String,
    ): RequestSpecificationBuilder<B> {
        headers += me.kpavlov.mokksy.containsHeader(headerName, headerValue)
        return this
    }

    public fun body(matcher: Matcher<String>): RequestSpecificationBuilder<B> {
        this.body += matcher
        return this
    }

    public fun priority(value: Int): RequestSpecificationBuilder<B> {
        this.priority = value
        return this
    }

    internal fun build(): RequestSpecification =
        RequestSpecification(
            method = method,
            path = path,
            headers = headers,
            body = body,
            priority = priority ?: DEFAULT_STUB_PRIORITY,
        )
}
