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
import androidx.compose.animation.core.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.librecybernetics.coopcycle.activities.LaunchActivity.Companion.configuredServerKey
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.screen.CooperativeSelectionScreen
import dev.librecybernetics.coopcycle.screen.WelcomeScreen
import dev.librecybernetics.coopcycle.util.preferencesDataStore
import dev.librecybernetics.location.LocationActivityService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class InitialSetup : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
    companion object {
        internal enum class Screen(val route: String) { Welcome("welcome"), Selection("selection") }

        private val coarseLocation: MutableStateFlow<Location?> = MutableStateFlow(null)

        @Composable
        private fun InitialSetupNavigation(
            cooperatives: StateFlow<Set<Cooperative>>,
            cooperativeOnClick: (Cooperative) -> Unit = {},
            extraFunction: () -> Unit = {}
        ) {
            val navController = rememberAnimatedNavController()
            AnimatedNavHost(navController, InitialSetup.Companion.Screen.Welcome.route) {
                composable(
                    InitialSetup.Companion.Screen.Welcome.route,
                    enterTransition = { _, _ ->
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            tween(500)
                        )
                    },
                    exitTransition = { _, _ ->
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            tween(500)
                        )
                    }
                ) {
                    WelcomeScreen { navController.navigate(InitialSetup.Companion.Screen.Selection.route) }
                }
                composable(InitialSetup.Companion.Screen.Selection.route) {
                    extraFunction()
                    CooperativeSelectionScreen(
                        cooperatives,
                        coarseLocation,
                        cooperativeOnClick
                    )
                }
            }
        }
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
        Log.d("InitialSetup.Location", coarseLocation.value.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InitialSetupNavigation(
                cooperatives = cooperatives,
                extraFunction = { updateLocation() },
                cooperativeOnClick =
                { cooperative ->
                    if (cooperative.coopcycle_url != null) {
                        lifecycleScope.launch {
                            preferencesDataStore.edit { preferences ->
                                preferences[configuredServerKey] =
                                    cooperative.coopcycle_url!!.address
                            }
                        }
                        startActivity(Intent(activity, MainActivity::class.java))
                    }
                }
            )
        }

        lifecycleScope.launch {
            locationManager = getSystemService(LocationManager::class.java)
            requestQueue = Volley.newRequestQueue(activity)
            fetchCooperatives() // Prefetch before they are needed
        }
    }
}