package dev.librecybernetics.coopcycle.util

import android.location.Location
import dev.librecybernetics.coopcycle.schema.Cooperative

fun location(cooperative: Cooperative): Location {
    val l = Location("")
    l.latitude = cooperative.latitude.latitude.toDouble()
    l.longitude = cooperative.longitude.longitude.toDouble()
    return l
}