package dev.librecybernetics.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CityName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}