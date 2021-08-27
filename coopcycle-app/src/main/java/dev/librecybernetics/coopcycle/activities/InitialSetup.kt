package dev.librecybernetics.coopcycle.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.R
import dev.librecybernetics.coopcycle.Util.location
import dev.librecybernetics.coopcycle.dao.CooperativeSummaryDAO
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.location.LocationActivityService
import kotlin.math.max

class InitialSetup : AppCompatActivity(), LocationActivityService, CooperativeSummaryDAO {
    companion object {
        private enum class Screen { Welcome, Selection }

        private data class CooperativeHolder(val view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.setOnClickListener {
                    Toast.makeText(
                        view.context,
                        "Selected ${currentCooperative?.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            private val cityNameView: TextView = view.findViewById(R.id.choose_city_card_city)
            private val countryCodeView: TextView =
                view.findViewById(R.id.choose_city_card_country_code)
            private val cooperativeNameView: TextView =
                view.findViewById(R.id.choose_city_card_coop_name)

            private var currentCooperative: Cooperative? = null
            fun updateWithCooperative(cooperative: Cooperative) {
                currentCooperative = cooperative
                cityNameView.text = cooperative.city.name
                countryCodeView.text = cooperative.country.code
                cooperativeNameView.text = cooperative.name.name
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
                )
            }

            override fun onBindViewHolder(holder: CooperativeHolder, position: Int) {
                holder.updateWithCooperative(sortedCooperatives[position])
            }

            override fun getItemCount(): Int {
                return cooperatives.size
            }
        }
    }

    // TODO: Move to Bundle to cache results
    private var cooperatives: Set<Cooperative> = setOf()
    private var currentScreen: Screen = Screen.Welcome

    override val activity: Activity = this
    override var locationManager: LocationManager? = null
    override var requestQueue: RequestQueue? = null

    private fun changeScreen(toScreen: Screen) {
        when (toScreen) {
            Screen.Welcome -> {
                setContentView(R.layout.initial_setup_welcome)
                val chooseCityButton: Button = findViewById(R.id.choose_city_button)
                chooseCityButton.setOnClickListener { changeScreen(Screen.Selection) }
            }
            Screen.Selection -> {
                setContentView(R.layout.initial_setup_selection)
                // Reload page with results; TODO: Investigate graceful recyclerview update
                cooperatives = fetchCooperatives().orEmpty()
                if (cooperatives.isNotEmpty()) {
                    val chooseCityRecyclerView: RecyclerView =
                        findViewById(R.id.choose_city_recycler_view)
                    chooseCityRecyclerView.adapter = CooperativeCardRecyclerAdapter(cooperatives)
                    chooseCityRecyclerView.layoutManager = LinearLayoutManager(this)
                    val coarseLocation: Location? = getLocation(
                        LocationActivityService.Companion.LocationFreshness.Any,
                        LocationActivityService.Companion.LocationAccuracy.Coarse
                    )
                    Log.d("SELECT.COOPERATIVES.LOCATION", coarseLocation.toString())
                    val closestCoop: Cooperative? = cooperatives.sortedBy {
                        coarseLocation?.distanceTo(location(it))
                    }.getOrNull(0)
                    Log.d("SELECT.COOPERATIVES.CLOSEST", closestCoop.toString())
                    if (closestCoop != null) {
                        val position = cooperatives.sortedBy { it.city.name }
                            .indexOfFirst { it.city.name == closestCoop.city.name }
                        chooseCityRecyclerView.scrollToPosition(max(position - 3, 0))
                    }
                }
            }
        }
        currentScreen = toScreen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeScreen(Screen.Welcome)

        locationManager = getSystemService(LocationManager::class.java)
        requestQueue = Volley.newRequestQueue(this)
        fetchCooperatives() // Prefetch before they are needed
    }

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