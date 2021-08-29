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

    LazyColumn(
        state = listState
    ) {
        // Determine if there is a close by coop)
        val closestCoop: Cooperative? = observedCooperatives
            .filter { observedCoarseLocation != null }
            .sortedBy { observedCoarseLocation?.distanceTo(location(it)) }
            .getOrNull(0)
        Log.d("SELECT.COOPERATIVES.CLOSEST", closestCoop.toString())

        // Position on the list; if non then -1
        val closestCoopPosition = observedCooperatives
            .sortedBy { it.city.name }
            .indexOfFirst { it.city.name == closestCoop?.city?.name }

        if (closestCoop != null) {
            composableScope.launch {
                listState.animateScrollToItem(max(closestCoopPosition - 3, 0))
            }
        }

        itemsIndexed(observedCooperatives.sortedBy { it.city.name }) { i: Int, cooperative: Cooperative ->
            CooperativeCard(
                cooperative,
                { cooperativeOnClick(cooperative) },
                highlight = i == closestCoopPosition
            )
        }
    }
}