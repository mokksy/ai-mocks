package me.kpavlov.mokksy

import io.kotest.matchers.Matcher
import io.kotest.matchers.string.contain
import io.ktor.http.HttpMethod
import io.ktor.server.request.ApplicationRequest
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.receive

/**
 * Represents the default priority assigned to a mapping or configuration.
 *
 * This constant is commonly used to define the priority level of a specific process, mapping, or
 * configuration when no explicit priority is provided. Lower numerical values generally indicate
 * higher priority.
 */
public const val DEFAULT_PRIORITY: Int = 0

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
 * @property body List of matchers for the request body as a string. All matchers must pass for a match to succeed.
 * @property priority The priority value used for comparing different specifications.
 * Lower values indicate higher priority.
 */
public open class RequestSpecification(
    public val method: Matcher<HttpMethod>? = null,
    public val path: Matcher<String>? = null,
    public val body: List<Matcher<String>> = listOf<Matcher<String>>(),
    public val priority: Int = DEFAULT_PRIORITY,
) : Comparable<RequestSpecification> {
    public suspend fun matches(request: ApplicationRequest): Boolean =
        (method == null || method.test(request.httpMethod).passed()) &&
            (path == null || path.test(request.path()).passed()) &&
            (matchBodyString(body, request))

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
        val bodyString = request.call.receive(String::class)
        return bodyMatchers.all {
            it
                .test(bodyString)
                .passed()
        }
    }

    override fun compareTo(other: RequestSpecification): Int =
        this.priority.compareTo(other.priority)

    internal fun toDescription(): String = "method: $method, path: $path, body: $body"
}

public open class RequestSpecificationBuilder<B : RequestSpecificationBuilder<B>> {
    protected var method: Matcher<HttpMethod>? = null
    public var path: Matcher<String>? = null
    public val body: MutableList<Matcher<String>> = mutableListOf<Matcher<String>>()
    protected var priority: Int = DEFAULT_PRIORITY

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
            body = body,
            priority = priority,
        )
}
