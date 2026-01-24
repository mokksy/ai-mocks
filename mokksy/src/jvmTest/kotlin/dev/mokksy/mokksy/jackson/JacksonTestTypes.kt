package dev.mokksy.mokksy.jackson

import com.fasterxml.jackson.annotation.JsonProperty

internal data class JacksonInput(
    @param:JsonProperty val name: String,
)

internal data class JacksonOutput(
    @param:JsonProperty("pikka-hi")
    val greeting: String,
)
