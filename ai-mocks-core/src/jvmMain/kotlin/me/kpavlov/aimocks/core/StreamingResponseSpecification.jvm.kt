package me.kpavlov.aimocks.core

import kotlinx.coroutines.stream.consumeAsFlow
import java.util.stream.Stream

public fun <P : Any, T : Any, R : Any> AbstractStreamingResponseSpecification<P, T, R>.responseStream(
    stream: Stream<T>,
) {
    responseFlow = stream.consumeAsFlow()
}
