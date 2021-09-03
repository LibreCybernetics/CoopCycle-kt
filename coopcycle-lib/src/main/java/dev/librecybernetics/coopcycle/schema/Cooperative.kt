package dev.librecybernetics.coopcycle.schema

import dev.librecybernetics.coopcycle.types.CoopcycleServerAddress
import dev.librecybernetics.coopcycle.types.CooperativeEMailAddress
import dev.librecybernetics.coopcycle.types.CooperativeName
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode
import dev.librecybernetics.types.Latitude
import dev.librecybernetics.types.Longitude
import dev.librecybernetics.util.KUppercaseSerializer
import dev.librecybernetics.util.lenientJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.math.BigDecimal

@Serializable
private data class LaxCooperative(
    val name: String,
    val mail: String? = null,
    val city: String,
    @Serializable(with = KUppercaseSerializer::class)
    val country: String,
    val coopcycle_url: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)

private fun validate(cooperative: LaxCooperative): Cooperative? =
    try {
        if (cooperative.mail !== null && cooperative.coopcycle_url !== null && cooperative.latitude !== null && cooperative.longitude !== null)
            Cooperative(
                CooperativeName(cooperative.name),
                CooperativeEMailAddress(cooperative.mail),
                CityName(cooperative.city),
                CountryCode(cooperative.country),
                CoopcycleServerAddress(cooperative.coopcycle_url),
                Latitude(BigDecimal(cooperative.latitude)),
                Longitude(BigDecimal(cooperative.longitude)),
            ) else null
    } catch (t: Throwable) {
        null
    }

@Serializable
data class Cooperative(
    val name: CooperativeName,
    val mail: CooperativeEMailAddress,
    val city: CityName,
    val country: CountryCode,
    val coopcycle_url: CoopcycleServerAddress,
    val latitude: Latitude,
    val longitude: Longitude
) {
    @ExperimentalSerializationApi
    companion object {
        fun setFromJSONString(string: String): Set<Cooperative> =
            lenientJson
                .decodeFromString<Set<LaxCooperative>>(string)
                .mapNotNull { validate(it) }
                .toSet()

        fun toJSONString(cooperatives: Set<Cooperative>) = lenientJson.encodeToString(cooperatives)
    }
}
