package me.kpavlov.aimocks.a2a.model

/**
 * Abstract DSL builder for Error classes.
 */
@Suppress("UNCHECKED_CAST")
public open class JSONRPCErrorBuilder<E : JSONRPCError, T> {
    public var code: Long? = null
    public var message: String? = null
    public var data: Data? = null

    /**
     * Sets the error code.
     *
     * @param code The error code.
     * @return This builder instance for method chaining.
     */
    public fun code(code: Long): T =
        apply {
            this.code = code
        } as T

    /**
     * Sets the error message.
     *
     * @param message The error message.
     * @return This builder instance for method chaining.
     */
    public fun message(message: String): T =
        apply {
            this.message = message
        } as T

    /**
     * Sets the additional error data.
     *
     * @param data The additional error data.
     * @return This builder instance for method chaining.
     */
    public fun data(data: Data?): T =
        apply {
            this.data = data
        } as T

    /**
     * Builds an [E] class instance with the configured parameters.
     *
     * @return A new [E] instance.
     */
    public open fun build(): E =
        JSONRPCError(
            code = requireNotNull(code) { "JSONRPCError.code must be provided" },
            message = requireNotNull(message) { "JSONRPCError.message must be provided" },
            data = data,
        ) as E
}
