package dev.librecybernetics.coopcycle.activities

import android.app.Activity
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.R
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.types.CooperativeName
import dev.librecybernetics.coopcycle.util.location
import dev.librecybernetics.location.LocationActivityService
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode
import kotlinx.coroutines.flow.*

class InitialSetup : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
    companion object {
        private enum class Screen { Welcome, Selection }

        private data class CooperativeHolder(val view: ComposeView) :
            RecyclerView.ViewHolder(view) {
            private lateinit var currentCooperative: Cooperative

            @ExperimentalMaterialApi
            fun updateWithCooperative(cooperative: Cooperative) {
                currentCooperative = cooperative
                view.setContent {
                    CooperativeCard(
                        cooperative.city,
                        cooperative.name,
                        cooperative.country
                    )
                }
            }
        }

        @Composable
        @ExperimentalMaterialApi
        fun CooperativeCard(
            cityName: CityName,
            cooperativeName: CooperativeName,
            countryCode: CountryCode
        ) {
            Card(onClick = { }, elevation = 10.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp, 5.dp)
                ) {
                    Column {
                        Text(cityName.name)
                        Text(cooperativeName.name)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(countryCode.code)
                }
            }
        }

        private data class CooperativeCardRecyclerAdapter(val cooperatives: Set<Cooperative>) :
            RecyclerView.Adapter<CooperativeHolder>() {
            private val sortedCooperatives = cooperatives.sortedBy { it.city.name }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CooperativeHolder {
                return CooperativeHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.initial_setup_cooperative_card, parent, false)
                            as ComposeView
                )
            }

            @ExperimentalMaterialApi
            override fun onBindViewHolder(holder: CooperativeHolder, position: Int) {
                holder.updateWithCooperative(sortedCooperatives[position])
            }

            override fun getItemCount(): Int {
                return cooperatives.size
            }
        }
    }

    // TODO: Move to Bundle to cache results
    override val cooperatives: MutableStateFlow<Set<Cooperative>> = MutableStateFlow(setOf())
    private var currentScreen: Screen = Screen.Welcome

    override val activity: Activity = this
    override lateinit var locationManager: LocationManager
    override lateinit var requestQueue: RequestQueue

    @Composable
    @ExperimentalMaterialApi
    private fun CooperativesList(
        cooperatives: StateFlow<Set<Cooperative>>
    ) {
        val observedCooperatives by cooperatives.collectAsState()
        LazyColumn {
            items(observedCooperatives.sortedBy { it.city.name }) { cooperative: Cooperative ->
                CooperativeCard(cooperative.city, cooperative.name, cooperative.country)
            }
        }
    }

    @ExperimentalMaterialApi
    private fun changeScreen(toScreen: Screen) {
        when (toScreen) {
            Screen.Welcome -> {
                setContentView(R.layout.initial_setup_welcome)
                val chooseCityButton: Button = findViewById(R.id.choose_city_button)
                chooseCityButton.setOnClickListener { changeScreen(Screen.Selection) }
            }
            Screen.Selection -> {
                setContentView(R.layout.initial_setup_selection)
                val chooseCityComposeView: ComposeView =
                    findViewById(R.id.choose_city_compose_view)
                chooseCityComposeView.setContent {
                    CooperativesList(cooperatives)
                }
                val coarseLocation: Location? = getLocation(
                    LocationActivityService.Companion.LocationFreshness.Any,
                    LocationActivityService.Companion.LocationAccuracy.Coarse
                )
                Log.d("SELECT.COOPERATIVES.LOCATION", coarseLocation.toString())
                val closestCoop: Cooperative? = cooperatives.value.sortedBy {
                    coarseLocation?.distanceTo(location(it))
                }.getOrNull(0)
                Log.d("SELECT.COOPERATIVES.CLOSEST", closestCoop.toString())
//                    if (closestCoop != null) {
//                        val position = cooperatives.sortedBy { it.city.name }
//                            .indexOfFirst { it.city.name == closestCoop.city.name }
//                        chooseCityComposeView.scrollToPosition(max(position - 3, 0))
//                    }
            }
        }
        currentScreen = toScreen
    }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeScreen(Screen.Welcome)

        locationManager = getSystemService(LocationManager::class.java)
        requestQueue = Volley.newRequestQueue(this)
        requestCooperatives() // Prefetch before they are needed
    }

    @ExperimentalMaterialApi
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> when (currentScreen) {
                Screen.Selection -> {
                    changeScreen(Screen.Welcome)
                    true // required for return
                }
                else -> super.onKeyDown(keyCode, event)
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}