package dev.librecybernetics.coopcycle.types

import dev.librecybernetics.coopcycle.Util
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@JvmInline
@Serializable
value class Longitude(
    @Serializable(with = Util.DecimalAsStringSerializer::class) val longitude: BigDecimal
) {
    init {
        require(longitude >= BigDecimal(-180))
        require(longitude <= BigDecimal(180))
    }
}
