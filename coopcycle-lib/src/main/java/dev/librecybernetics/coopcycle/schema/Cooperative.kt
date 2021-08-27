package dev.librecybernetics.coopcycle.schema

import dev.librecybernetics.coopcycle.types.*
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode
import dev.librecybernetics.types.Latitude
import dev.librecybernetics.types.Longitude
import dev.librecybernetics.util.lenientJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

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
) {
    companion object {
        fun setFromString(string: String) = lenientJson.decodeFromString<Set<Cooperative>>(string)
    }
}
