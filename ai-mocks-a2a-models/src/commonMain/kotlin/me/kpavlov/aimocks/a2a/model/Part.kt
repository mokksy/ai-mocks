package me.kpavlov.aimocks.a2a.model

import kotlinx.serialization.Serializable
import me.kpavlov.aimocks.a2a.model.serializers.PartSerializer

@Serializable(with = PartSerializer::class)
public sealed interface Part
