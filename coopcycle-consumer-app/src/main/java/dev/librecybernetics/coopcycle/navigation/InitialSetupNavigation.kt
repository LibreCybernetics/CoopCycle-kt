package dev.librecybernetics.coopcycle.navigation

import android.location.Location
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.google.accompanist.navigation.animation.*
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.screen.CooperativeSelectionScreen
import dev.librecybernetics.coopcycle.screen.WelcomeScreen
import kotlinx.coroutines.flow.*

object InitialSetupNavigation {
	internal enum class Screen(val route: String) {
		Welcome("welcome"),
		Selection("selection")
	}
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun InitialSetupNavigation(
	cooperatives: StateFlow<Set<Cooperative>>,
	location: StateFlow<Location?>,
	cooperativeOnClick: (Cooperative) -> Unit = {},
	extraFunction: () -> Unit = {}
) {
	val navController = rememberAnimatedNavController()
	AnimatedNavHost(navController, InitialSetupNavigation.Screen.Welcome.route) {
		composable(
			InitialSetupNavigation.Screen.Welcome.route,
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
		) { WelcomeScreen { navController.navigate(InitialSetupNavigation.Screen.Selection.route) } }
		composable(InitialSetupNavigation.Screen.Selection.route) {
			extraFunction()
			CooperativeSelectionScreen(cooperatives, location, cooperativeOnClick)
		}
	}
}
