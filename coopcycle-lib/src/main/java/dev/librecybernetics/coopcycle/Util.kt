package dev.librecybernetics.coopcycle

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

object Util {
    // Taken from: https://petnagy.medium.com/kotlinx-serialization-part3-4bb41a33c1a3
    object DecimalAsStringSerializer : KSerializer<BigDecimal> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("decimal", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value.toPlainString())
        }

        override fun deserialize(decoder: Decoder): BigDecimal {
            val string = decoder.decodeString()
            return BigDecimal(string)
        }
    }
}