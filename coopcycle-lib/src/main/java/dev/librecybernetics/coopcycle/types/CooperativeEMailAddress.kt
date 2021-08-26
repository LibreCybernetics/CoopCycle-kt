package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CooperativeEMailAddress(val email: String)
