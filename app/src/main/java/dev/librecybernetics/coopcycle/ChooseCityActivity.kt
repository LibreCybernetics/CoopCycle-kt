package dev.librecybernetics.coopcycle

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
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.schema.Cooperative
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChooseCityActivity : AppCompatActivity() {
    companion object {
        private enum class Screen { Welcome, Selection }

        private val jsonFormat = Json { isLenient = true; ignoreUnknownKeys = true }

        private data class CooperativeHolder(val view: View) : RecyclerView.ViewHolder(view) {
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
                cooperativeNameView.text = "Cooperative: ${cooperative.name.name}"
            }
        }

        private data class CooperativeCardRecyclerAdapter(val cooperatives: Set<Cooperative>) :
            RecyclerView.Adapter<CooperativeHolder>() {
            private val sortedCooperatives = cooperatives.sortedBy { it.city.name }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CooperativeHolder {
                return CooperativeHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.city_selection_cooperative_card, parent, false)
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

    private val cooperativesRequest: StringRequest =
        StringRequest(
            Request.Method.GET,
            "https://coopcycle.org/coopcycle.json",
            { processResponse(it) },
            { processError(it) }
        )

    // TODO: Move to Bundle to cache results
    private var cooperatives: Set<Cooperative> = setOf()
    private var currentScreen: Screen = Screen.Welcome
    private var volleyRequestQueue: RequestQueue? = null

    private fun changeScreen(toScreen: Screen) {
        when (toScreen) {
            Screen.Welcome -> {
                setContentView(R.layout.welcome_screen)
                val chooseCityButton: Button = findViewById(R.id.choose_city_button)
                chooseCityButton.setOnClickListener { changeScreen(Screen.Selection) }
            }
            Screen.Selection -> {
                setContentView(R.layout.city_selection)
                if (cooperatives.isNotEmpty()) {
                    val chooseCityRecyclerView: RecyclerView =
                        findViewById(R.id.choose_city_recycler_view)
                    chooseCityRecyclerView.adapter = CooperativeCardRecyclerAdapter(cooperatives)
                    chooseCityRecyclerView.layoutManager = LinearLayoutManager(this)
                }
            }
        }
        currentScreen = toScreen
    }

    private fun processError(error: VolleyError) {
        Log.e("FETCH.COOPERATIVES", error.message.orEmpty())
        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun processResponse(response: String) {
        cooperatives = jsonFormat
            .decodeFromString<List<Cooperative>>(response)
            .toSet()
        // Reload page with results; TODO: Investigate graceful recyclerview update
        if (currentScreen == Screen.Selection) changeScreen(Screen.Selection)
        if (cooperatives.isEmpty()) {
            Log.w("FETCH.COOPERATIVES", "Successfully got zero cooperatives")
        } else {
            Log.i("FETCH.COOPERATIVES", "Successfully got list of cooperatives")
            Log.v(
                "FETCH.COOPERATIVES",
                cooperatives.map { it.name }.toString()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeScreen(Screen.Welcome)

        volleyRequestQueue = Volley.newRequestQueue(this)
        volleyRequestQueue?.add(cooperativesRequest)
        volleyRequestQueue?.start()
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