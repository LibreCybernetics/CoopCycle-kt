package dev.librecybernetics.coopcycle.types

import dev.librecybernetics.coopcycle.Util
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@JvmInline
@Serializable
value class Latitude(
    @Serializable(with = Util.DecimalAsStringSerializer::class) val latitude: BigDecimal
) {
    init {
        require(latitude >= BigDecimal(-90))
        require(latitude <= BigDecimal(90))
    }
}
