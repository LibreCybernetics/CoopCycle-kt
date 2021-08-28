package dev.librecybernetics.coopcycle.activities

import android.app.Activity
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.screen.CooperativeSelectionScreen
import dev.librecybernetics.coopcycle.screen.WelcomeScreen
import dev.librecybernetics.location.LocationActivityService
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
class InitialSetup : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
    companion object {
        private enum class Screen { Welcome, Selection }
    }

    private val currentScreen: MutableStateFlow<Screen> = MutableStateFlow(Screen.Welcome)
    private val coarseLocation: MutableStateFlow<Location?> = MutableStateFlow(null)

    override val activity: Activity = this
    override lateinit var locationManager: LocationManager
    override lateinit var requestQueue: RequestQueue

    private fun updateLocation() {
        coarseLocation.value = getLocation(
            LocationActivityService.Companion.LocationFreshness.Any,
            LocationActivityService.Companion.LocationAccuracy.Coarse
        )
        Log.d("SELECT.COOPERATIVES.LOCATION", coarseLocation.value.toString())
    }

    private fun changeScreen(toScreen: Screen) {
        currentScreen.update {
            when (toScreen) {
                Screen.Welcome -> setContent {
                    WelcomeScreen { changeScreen(Screen.Selection) }
                }
                Screen.Selection -> {
                    setContent {
                        CooperativeSelectionScreen(cooperatives, coarseLocation)
                    }
                    updateLocation()
                }
            }
            toScreen
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeScreen(Screen.Welcome)

        locationManager = getSystemService(LocationManager::class.java)
        requestQueue = Volley.newRequestQueue(this)
        fetchCooperatives() // Prefetch before they are needed
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode to currentScreen.value) {
            KeyEvent.KEYCODE_BACK to Screen.Selection -> {
                changeScreen(Screen.Welcome)
                true // required for return
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}