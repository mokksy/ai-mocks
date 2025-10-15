package dev.mokksy.mokksy

/**
 * Configuration class for defining the behavior and attributes of a stub.
 *
 * This data class is used to configure the characteristics of a stub,
 * including its name, removal behavior, and logging verbosity.
 *
 * @property name The name of the stub configuration. Can be null if not provided.
 * @property removeAfterMatch Indicates whether the stub should be automatically removed
 *        after a successful match with a request.
 * @property verbose Configures detailed logging for the stub, enabling or disabling
 *        additional logging information.
 * @author Konstantin Pavlov
 */
public data class StubConfiguration(
    val name: String? = null,
    val removeAfterMatch: Boolean = false,
    val verbose: Boolean = false,
)
