package dev.librecybernetics.coopcycle.component

import android.location.Location
import android.util.Log
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.util.location
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.max

@Composable
@ExperimentalMaterialApi
fun CooperativesList(
    cooperatives: StateFlow<Set<Cooperative>>,
    coarseLocation: StateFlow<Location?> = MutableStateFlow(null)
) {
    val listState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()

    val observedCooperatives by cooperatives.collectAsState()
    val observedCoarseLocation by coarseLocation.collectAsState()

    LazyColumn(
        state = listState
    ) {
        composableScope.launch {
            // Jump to a close-by coop
            val closestCoop: Cooperative? = observedCooperatives.sortedBy {
                observedCoarseLocation?.distanceTo(location(it))
            }.getOrNull(0)
            Log.d("SELECT.COOPERATIVES.CLOSEST", closestCoop.toString())
            if (closestCoop != null) {
                val position = observedCooperatives
                    .sortedBy { it.city.name }
                    .indexOfFirst { it.city.name == closestCoop.city.name }
                listState.animateScrollToItem(max(position - 1, 0))
            }
        }

        items(observedCooperatives.sortedBy { it.city.name }) { cooperative: Cooperative ->
            CooperativeCard(cooperative.city, cooperative.name, cooperative.country)
        }
    }
}