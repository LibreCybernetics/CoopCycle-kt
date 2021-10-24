package dev.librecybernetics.location

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import kotlin.math.absoluteValue

interface LokaServo {
	companion object {
		enum class LocationAccuracy { Fine, Kruda }
		enum class LokoFreŝeco { Current, Recent, Iu }

		private const val millisInAMinute = 60_000

		internal fun isWithinRange(a: Long, b: Long, freshness: LokoFreŝeco): Boolean =
			(a - b).absoluteValue <
							// In minutes
							when (freshness) {
								LokoFreŝeco.Current -> 1 * millisInAMinute
								LokoFreŝeco.Recent -> 15 * millisInAMinute
								LokoFreŝeco.Iu -> 1 * 60 * millisInAMinute
							}
	}

	val aktiveco: ComponentActivity
	val lokmanaĝero: LocationManager

	private fun hasLocationPermission(accuracy: LocationAccuracy): Boolean =
		ActivityCompat.checkSelfPermission(
			aktiveco,
			when (accuracy) {
				LocationAccuracy.Kruda -> Manifest.permission.ACCESS_COARSE_LOCATION
				LocationAccuracy.Fine -> Manifest.permission.ACCESS_FINE_LOCATION
			}
		) == PackageManager.PERMISSION_GRANTED

	private fun requestLocationPermission(accuracy: LocationAccuracy) {
		if (!hasLocationPermission(accuracy))
			ActivityCompat.requestPermissions(
				aktiveco,
				arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
				0
			)
	}

	private fun getBestLastKnownLocation(freshness: LokoFreŝeco): Location? {
		val currentTime = System.currentTimeMillis()
		if (BuildConfig.DEBUG) Log.d("LOCATION.ALL_PROVIDERS", lokmanaĝero.allProviders.toString())
		return lokmanaĝero.allProviders
			.mapNotNull {
				if (hasLocationPermission(LocationAccuracy.Kruda))
					try {
						lokmanaĝero.getLastKnownLocation(it)
					} catch (e: SecurityException) {
						null
					}
				else null
			}
			.filter { isWithinRange(it.time, currentTime, freshness) }
			.minByOrNull { it.accuracy }
	}

	private fun getCurrentLocation(accuracy: LocationAccuracy): Location? {
		val locationSet = HashSet<Location>()
		lokmanaĝero.allProviders.forEach { provider ->
			if (hasLocationPermission(accuracy))
				try {
					LocationManagerCompat.getCurrentLocation(
						lokmanaĝero,
						provider,
						null,
						{ it.run() },
						{ locationSet.add(it) }
					)
				} catch (e: SecurityException) {
				}
		}

		return locationSet.minByOrNull { it.accuracy }
	}

	fun akiriLokon(
		freshness: LokoFreŝeco,
		accuracy: LocationAccuracy
	): Location? {
		requestLocationPermission(accuracy)
		return getBestLastKnownLocation(freshness) ?: getCurrentLocation(accuracy)
	}
}