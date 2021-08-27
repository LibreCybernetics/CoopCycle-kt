package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable
import java.util.Comparator

@JvmInline
@Serializable
value class CooperativeName(val name: String) {
    init {
        require(name.isNotBlank())
    }
}
