package dev.librecybernetics.types

import kotlinx.serialization.Serializable

@Serializable
data class Geolocation(
    val latitude: Latitude,
    val longitude: Longitude
)
