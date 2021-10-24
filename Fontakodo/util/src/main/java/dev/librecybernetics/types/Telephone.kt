package dev.librecybernetics.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Telephone(val telephone: String)
