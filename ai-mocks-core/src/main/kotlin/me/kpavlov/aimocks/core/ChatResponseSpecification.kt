package me.kpavlov.aimocks.core

import me.kpavlov.mokksy.AbstractResponseDefinition

public open class ChatResponseSpecification<T>(
    protected val response: AbstractResponseDefinition<T>,
)
