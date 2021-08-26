package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable
import java.util.Comparator

@JvmInline
@Serializable
value class CooperativeName(val name: String) {
    companion object {
        object LexicographicalComparator : Comparator<CityName> {
            override fun compare(c1: CityName, c2: CityName): Int = c1.name.compareTo(c2.name)
        }
    }
}
