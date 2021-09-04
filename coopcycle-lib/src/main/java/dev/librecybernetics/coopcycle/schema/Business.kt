package dev.librecybernetics.coopcycle.schema

import dev.librecybernetics.coopcycle.types.BusinessName
import dev.librecybernetics.util.lenientJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@JvmInline
@Serializable
value class BusinessId(val id: Int)

@Serializable
private data class LaxBusiness(
    val id: Int,
    val name: String,
    val enabled: Boolean? = null,
    val address: Address? = null
)

private fun validate(business: LaxBusiness): Business? =
    try {
        if (business.enabled != null && business.address != null)
            Business(
                BusinessId(business.id),
                BusinessName(business.name),
                business.enabled,
                business.address
            ) else null
    } catch (t: Throwable) {
        null
    }

@Serializable
data class Business(
    val id: BusinessId,
    val name: BusinessName,
    val enabled: Boolean,
    val address: Address,
) {
    @ExperimentalSerializationApi
    companion object {
        fun mapFromJSONString(string: String): Map<BusinessId, Business> =
            lenientJson
                .decodeFromString<Set<LaxBusiness>>(string)
                .mapNotNull { validate(it) }
                .map { it.id to it }.toMap()
    }
}