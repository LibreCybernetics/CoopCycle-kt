package dev.librecybernetics.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

// Taken from: https://petnagy.medium.com/kotlinx-serialization-part3-4bb41a33c1a3
object KBigDecimalStringSerializer : KSerializer<BigDecimal> {
	override val descriptor: SerialDescriptor =
		PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

	override fun serialize(encoder: Encoder, value: BigDecimal): Unit =
		encoder.encodeString(value.toPlainString())

	override fun deserialize(decoder: Decoder): BigDecimal =
		BigDecimal(decoder.decodeString())
}