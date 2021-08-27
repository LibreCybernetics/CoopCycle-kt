package dev.librecybernetics.types

import dev.librecybernetics.util.KBigDecimalStringSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@JvmInline
@Serializable
value class Latitude(
    @Serializable(with = KBigDecimalStringSerializer::class) val latitude: BigDecimal
) {
    init {
        require(latitude >= BigDecimal(-90))
        require(latitude <= BigDecimal(90))
    }
}