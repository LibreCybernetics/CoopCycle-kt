package dev.librecybernetics.coopcycle.schema

import dev.librecybernetics.coopcycle.types.*
import kotlinx.serialization.Serializable

@Serializable
data class Cooperative(
    val name: CooperativeName,
    val mail: CooperativeEMailAddress? = null,
    val city: CityName,
    val country: CountryCode,
    val coopcycle_url: CoopcycleServerAddress? = null,
    // val facebook_url: String; Not sure if needed.
    val latitude: Latitude,
    val longitude: Longitude
)
