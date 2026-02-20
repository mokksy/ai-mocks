package dev.mokksy.mokksy

import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig

/**
 * Controls which requests are recorded in the [dev.mokksy.mokksy.request.RequestJournal].
 */
public enum class JournalMode {
    /**
     * Records only requests with no matching stub.
     * Lower overhead; sufficient for [MokksyServer.checkForUnmatchedRequests].
     */
    LEAN,

    /**
     * Records all incoming requests, differentiating matched from unmatched.
     * Enables inspection of both [MokksyServer.findAllUnmatchedRequests] and matched requests.
     */
    FULL,
}

/**
 * Represents the configuration parameters for a server.
 *
 * This class includes options for logging verbosity, server naming, and content negotiation
 * setup. It allows customization of server behavior through its properties.
 *
 * @property verbose Determines whether detailed logging is enabled. When set to `true`,
 *                   verbose logging is enabled for debugging purposes. Default is `false`.
 * @property name The name of the server. Can be `null`, but defaults to "Mokksy" if not provided.
 *                Used for identification or descriptive purposes.
 * @property journalMode Controls which requests are recorded in the [dev.mokksy.mokksy.request.RequestJournal].
 *                       Defaults to [JournalMode.LEAN] (only unmatched requests).
 * @property contentNegotiationConfigurer A function used to configure content negotiation for
 *                                        the server. The provided function is invoked with
 *                                        a `ContentNegotiationConfig` as its parameter.
 *                                        Defaults to a platform-specific implementation.
 * @author Konstantin Pavlov
 */
public data class ServerConfiguration(
    val verbose: Boolean = false,
    val name: String? = "Mokksy",
    val journalMode: JournalMode = JournalMode.LEAN,
    val contentNegotiationConfigurer: (
        ContentNegotiationConfig,
    ) -> Unit = ::configureContentNegotiation,
)
