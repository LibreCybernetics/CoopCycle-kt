package dev.librecybernetics.coopcycle.util

import android.app.Activity
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.librecybernetics.coopcycle.schema.Cooperative

fun location(cooperative: Cooperative): Location {
	val l = Location("")
	l.latitude = cooperative.latitude.latitude.toDouble()
	l.longitude = cooperative.longitude.longitude.toDouble()
	return l
}

val Activity.preferojDatumbutiko: DataStore<Preferences> by preferencesDataStore("settings")