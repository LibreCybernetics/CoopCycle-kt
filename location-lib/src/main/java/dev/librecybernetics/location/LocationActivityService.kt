package dev.librecybernetics.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import java.util.concurrent.Executor
import kotlin.math.absoluteValue

interface LocationActivityService {
    companion object {
        enum class LocationAccuracy { Fine, Coarse }
        enum class LocationFreshness { Current, Recent, Any }

        private const val milisInAMinute = 60_000
        private val directExecutor = DirectExecutor()

        private fun isWithinRange(a: Long, b: Long, freshness: LocationFreshness): Boolean =
            (a - b).absoluteValue <
                    // In minutes
                    when (freshness) {
                        LocationFreshness.Current -> 1 * milisInAMinute
                        LocationFreshness.Recent -> 60 * milisInAMinute
                        LocationFreshness.Any -> 24 * 60 * milisInAMinute
                    }

        private class DirectExecutor : Executor {
            override fun execute(r: Runnable) {
                r.run()
            }
        }
    }

    val activity: Activity
    var locationManager: LocationManager?

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
        Log.d("LOCATION.ALL_PROVIDERS", locationManager?.allProviders.toString())
        return locationManager?.allProviders.orEmpty()
            .mapNotNull {
                if (hasLocationPermission(LocationAccuracy.Coarse))
                    try {
                        locationManager?.getLastKnownLocation(it)
                    } catch (e: SecurityException) {
                        return null
                    }
                else null
            }
            .map { Log.v("LOCATION.${it.provider}", it.toString()); it }
            .filter { isWithinRange(it.time, currentTime, freshness) }
            .sortedBy { it.accuracy }
            .getOrNull(0)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(accuracy: LocationAccuracy): Location? {
        val locationSet = HashSet<Location>()
        locationManager?.allProviders.orEmpty().mapNotNull { provider ->
            if (hasLocationPermission(accuracy) && locationManager != null)
                try {
                    LocationManagerCompat.getCurrentLocation(
                        locationManager!!,
                        provider,
                        null,
                        directExecutor,
                        { location -> locationSet.add(location) }
                    )
                } catch (e: SecurityException) {
                    return null
                }
            else null
        }

        return locationSet.sortedBy { it.accuracy }.getOrNull(0)
    }

    fun getLocation(freshness: LocationFreshness, accuracy: LocationAccuracy): Location? {
        requestLocationPermission(accuracy)
        return getBestLastKnownLocation(freshness) ?: getCurrentLocation(accuracy)
    }
}