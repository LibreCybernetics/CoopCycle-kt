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
    coarseLocation: StateFlow<Location?> = MutableStateFlow(null),
    cooperativeOnClick: (Cooperative) -> Unit = { }
) {
    val listState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()

    val observedCooperatives by cooperatives.collectAsState()
    val observedCoarseLocation by coarseLocation.collectAsState()

    val orderedObservedCooperatives = observedCooperatives.sortedBy { it.city.name }

    // Determine if there is a close by coop
    val closestCoops: Set<Cooperative> = orderedObservedCooperatives
        .filter {
            val distance = observedCoarseLocation?.distanceTo(location(it))
            if (distance != null) distance <= 20_000 else false
        }.toSet()
    Log.d("SELECT.COOPERATIVES", closestCoops.toString())

    // Position on the list; if non then -1
    val closestCoopPosition = orderedObservedCooperatives.indexOfFirst { closestCoops.contains(it) }

    LazyColumn(
        state = listState
    ) {
        if (closestCoops.isNotEmpty()) {
            composableScope.launch {
                listState.animateScrollToItem(max(closestCoopPosition - 3, 0))
            }
        }

        items(orderedObservedCooperatives) { cooperative: Cooperative ->
            CooperativeCard(
                cooperative,
                { cooperativeOnClick(cooperative) },
                highlight = closestCoops.contains(cooperative)
            )
        }
    }
}