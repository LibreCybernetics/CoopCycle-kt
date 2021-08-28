package dev.librecybernetics.coopcycle.screen

import android.location.Location
import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.librecybernetics.coopcycle.component.CooperativesList
import dev.librecybernetics.coopcycle.schema.Cooperative
import kotlinx.coroutines.flow.*

@Composable
@ExperimentalMaterialApi
fun CooperativeSelectionScreen(
    cooperatives: StateFlow<Set<Cooperative>>,
    coarseLocation: StateFlow<Location?> = MutableStateFlow(null)
) {
    CooperativesList(cooperatives, coarseLocation)
}