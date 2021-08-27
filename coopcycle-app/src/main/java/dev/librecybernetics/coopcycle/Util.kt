package dev.librecybernetics.coopcycle

import android.location.Location
import androidx.annotation.Keep
import dev.librecybernetics.coopcycle.schema.Cooperative
import kotlinx.serialization.json.Json

object Util {
    fun location(cooperative: Cooperative): Location {
        val l = Location("")
        l.latitude = cooperative.latitude.latitude.toDouble()
        l.longitude = cooperative.longitude.longitude.toDouble()
        return l
    }
}