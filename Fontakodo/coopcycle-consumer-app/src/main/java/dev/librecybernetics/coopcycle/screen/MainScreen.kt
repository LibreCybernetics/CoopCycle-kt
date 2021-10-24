package dev.librecybernetics.coopcycle.screen

import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.librecybernetics.coopcycle.schema.Business
import kotlinx.coroutines.flow.*

@Composable
@ExperimentalMaterialApi
fun MainScreen(
	popularBusinesses: StateFlow<Set<Business>>,
	closebyBusinesses: StateFlow<Set<Business>>
) {
	Text("Hello")
}