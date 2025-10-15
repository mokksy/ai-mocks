package dev.mokksy.mokksy

import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig

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
 * @property contentNegotiationConfigurer A function used to configure content negotiation for
 *                                        the server. The provided function is invoked with
 *                                        a `ContentNegotiationConfig` as its parameter.
 *                                        Defaults to a platform-specific implementation.
 * @author Konstantin Pavlov
 */
public data class ServerConfiguration(
    val verbose: Boolean = false,
    val name: String? = "Mokksy",
    val contentNegotiationConfigurer: (
        ContentNegotiationConfig,
    ) -> Unit = ::configureContentNegotiation,
)
