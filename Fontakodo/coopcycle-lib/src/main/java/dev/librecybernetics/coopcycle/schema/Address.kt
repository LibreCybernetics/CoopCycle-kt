package dev.librecybernetics.coopcycle.schema

import dev.librecybernetics.types.Geolocation
import dev.librecybernetics.types.Telephone
import kotlinx.serialization.Serializable

@Serializable
data class Address(
	val name: String,
	val streetAddress: String,
	val telephone: Telephone,
	val geo: Geolocation,
)
