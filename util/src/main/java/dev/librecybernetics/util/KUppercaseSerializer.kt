package dev.librecybernetics.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class KUppercaseSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UppercaseString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: String) =
        encoder.encodeString(value.uppercase())

    override fun deserialize(decoder: Decoder): String = decoder.decodeString().uppercase()
}