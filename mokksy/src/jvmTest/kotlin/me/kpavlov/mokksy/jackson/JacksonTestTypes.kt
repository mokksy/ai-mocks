package me.kpavlov.mokksy.jackson

import com.fasterxml.jackson.annotation.JsonProperty

internal data class JacksonInput(
    @JsonProperty val name: String,
)

internal data class JacksonOutput(
    @JsonProperty("pikka-hi")
    val greeting: String,
)
