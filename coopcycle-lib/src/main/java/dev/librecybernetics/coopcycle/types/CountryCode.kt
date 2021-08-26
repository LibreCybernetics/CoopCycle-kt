package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CountryCode(val code: String) {
    init {
        require(code.length == 2)
        require(code.all { it.isLetter() })
    }
}
