package dev.librecybernetics.coopcycle.activities

import android.app.Activity
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.screen.CooperativeSelectionScreen
import dev.librecybernetics.coopcycle.screen.WelcomeScreen
import dev.librecybernetics.location.LocationActivityService
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class InitialSetup : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
    companion object {
        private enum class Screen(val route: String) { Welcome("welcome"), Selection("selection") }

        @Composable
        private fun InitialSetupNavigation(
            cooperatives: StateFlow<Set<Cooperative>>,
            coarseLocation: StateFlow<Location?>,
            extraFunction: () -> Unit = {}
        ) {
            val navController = rememberAnimatedNavController()
            AnimatedNavHost(navController, Screen.Welcome.route) {
                composable(
                    Screen.Welcome.route,
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
                    WelcomeScreen { navController.navigate(Screen.Selection.route) }
                }
                composable(Screen.Selection.route) {
                    extraFunction()
                    CooperativeSelectionScreen(
                        cooperatives,
                        coarseLocation
                    )
                }
            }
        }
    }

    private val coarseLocation: MutableStateFlow<Location?> = MutableStateFlow(null)

    override val activity: Activity = this
    override lateinit var locationManager: LocationManager
    override lateinit var requestQueue: RequestQueue

    private fun updateLocation() {
        coarseLocation.value = getLocation(
            LocationActivityService.Companion.LocationFreshness.Any,
            LocationActivityService.Companion.LocationAccuracy.Coarse
        )
        Log.d("InitialSetup.Location", coarseLocation.value.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitialSetupNavigation(
                cooperatives = cooperatives,
                coarseLocation = coarseLocation,
                extraFunction = { updateLocation() }
            )
        }

        locationManager = getSystemService(LocationManager::class.java)
        requestQueue = Volley.newRequestQueue(this)
        fetchCooperatives() // Prefetch before they are needed
    }
}