package dev.librecybernetics.coopcycle.activities

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.BuildConfig
import dev.librecybernetics.coopcycle.activities.LaunchActivity.Companion.configuredServerKey
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.navigation.InitialSetupNavigation
import dev.librecybernetics.coopcycle.util.preferencesDataStore
import dev.librecybernetics.location.LocationActivityService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class InitialSetupActivity : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
	companion object {
		private val coarseLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
	}

	override val activity: ComponentActivity = this
	override lateinit var locationManager: LocationManager
	override lateinit var requestQueue: RequestQueue

	private fun updateLocation() {
		val loc = getLocation(
			LocationActivityService.Companion.LocationFreshness.Any,
			LocationActivityService.Companion.LocationAccuracy.Coarse
		)
		coarseLocation.value = loc
		if (BuildConfig.DEBUG) Log.d("InitialSetup.Location", coarseLocation.value.toString())
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		locationManager = ActivityCompat.getSystemService(this, LocationManager::class.java)!!
		requestQueue = Volley.newRequestQueue(activity)

		setContent {
			InitialSetupNavigation(
				cooperatives = cooperatives,
				location = coarseLocation,
				extraFunction = { updateLocation() },
				cooperativeOnClick =
				{ cooperative ->
					lifecycleScope.launch {
						preferencesDataStore.edit { preferences ->
							preferences[configuredServerKey] = cooperative.coopcycle_url.address
						}
					}
					startActivity(Intent(activity, MainActivity::class.java))
				}
			)
		}

		lifecycleScope.launch {
			fetchCooperatives() // Prefetch before they are needed
		}
	}
}