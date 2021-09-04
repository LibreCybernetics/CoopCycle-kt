package dev.librecybernetics.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import kotlin.math.absoluteValue

interface LocationActivityService {
    companion object {
        enum class LocationAccuracy { Fine, Coarse }
        enum class LocationFreshness { Current, Recent, Any }

        private const val millisInAMinute = 60_000

        private fun isWithinRange(a: Long, b: Long, freshness: LocationFreshness): Boolean =
            (a - b).absoluteValue <
                    // In minutes
                    when (freshness) {
                        LocationFreshness.Current -> 1 * millisInAMinute
                        LocationFreshness.Recent -> 15 * millisInAMinute
                        LocationFreshness.Any -> 1 * 60 * millisInAMinute
                    }
    }

    val activity: ComponentActivity
    val locationManager: LocationManager

    private fun hasLocationPermission(accuracy: LocationAccuracy): Boolean =
        ActivityCompat.checkSelfPermission(
            activity,
            when (accuracy) {
                LocationAccuracy.Coarse -> Manifest.permission.ACCESS_COARSE_LOCATION
                LocationAccuracy.Fine -> Manifest.permission.ACCESS_FINE_LOCATION
            }
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission(accuracy: LocationAccuracy) {
        if (!hasLocationPermission(accuracy))
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
    }

    @SuppressLint("MissingPermission")
    private fun getBestLastKnownLocation(freshness: LocationFreshness): Location? {
        val currentTime = System.currentTimeMillis()
        Log.d("LOCATION.ALL_PROVIDERS", locationManager.allProviders.toString())
        return locationManager.allProviders
            .asSequence()
            .mapNotNull {
                if (hasLocationPermission(LocationAccuracy.Coarse))
                    try {
                        locationManager.getLastKnownLocation(it)
                    } catch (e: SecurityException) {
                        null
                    }
                else null
            }
            .filter { isWithinRange(it.time, currentTime, freshness) }
            .sortedBy { it.accuracy }
            .firstOrNull()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(accuracy: LocationAccuracy): Location? {
        val locationSet = HashSet<Location>()
        locationManager.allProviders.forEach { provider ->
            if (hasLocationPermission(accuracy))
                try {
                    LocationManagerCompat.getCurrentLocation(
                        locationManager,
                        provider,
                        null,
                        { it.run() },
                        { location: Location? ->
                            if (location != null) locationSet.add(location)
                        }
                    )
                } catch (e: SecurityException) {
                }
        }

        return locationSet.sortedBy { it.accuracy }.getOrNull(0)
    }

    fun getLocation(
        freshness: LocationFreshness,
        accuracy: LocationAccuracy
    ): Location? {
        requestLocationPermission(accuracy)
        return getBestLastKnownLocation(freshness) ?: getCurrentLocation(accuracy)
    }
}