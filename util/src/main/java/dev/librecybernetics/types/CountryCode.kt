package dev.librecybernetics.types

import dev.librecybernetics.util.KUppercaseSerializer
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CountryCode(
    @Serializable(with = KUppercaseSerializer::class)
    val code: String
) {
    init {
        require(code.length == 2)
        require(code.all { it.isLetter() && it.isUpperCase() })
    }
}