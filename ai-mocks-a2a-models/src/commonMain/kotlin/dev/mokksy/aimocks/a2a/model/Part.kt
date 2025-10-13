package dev.mokksy.aimocks.a2a.model

import dev.mokksy.aimocks.a2a.model.serializers.PartSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PartSerializer::class)
public sealed interface Part
