package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CoopcycleServerAddress(val address: String)
