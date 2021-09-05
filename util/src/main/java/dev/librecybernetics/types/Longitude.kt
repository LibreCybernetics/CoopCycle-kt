package dev.librecybernetics.types

import dev.librecybernetics.util.KBigDecimalStringSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@JvmInline
@Serializable
value class Longitude(
	@Serializable(with = KBigDecimalStringSerializer::class)
	val longitude: BigDecimal
) {
	init {
		require(longitude >= BigDecimal(-180))
		require(longitude <= BigDecimal(180))
	}
}