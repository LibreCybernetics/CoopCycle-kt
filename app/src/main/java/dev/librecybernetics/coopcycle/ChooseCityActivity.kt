package dev.librecybernetics.coopcycle

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.types.CityName
import dev.librecybernetics.coopcycle.types.CountryCode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChooseCityActivity : AppCompatActivity() {
    companion object {
        private enum class Screen { Welcome, Selection }

        private val jsonFormat = Json { isLenient = true; ignoreUnknownKeys = true }
    }

    private val cooperativesRequest: StringRequest =
        StringRequest(
            Request.Method.GET,
            "https://coopcycle.org/coopcycle.json",
            { processResponse(it) },
            { processError(it) }
        )

    // TODO: Move to Bundle to cache results
    private var cooperatives: Map<Pair<CountryCode, CityName>, Cooperative>? = null
    private var currentScreen: Screen? = null
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
                if(!cooperatives.isNullOrEmpty()) {
                    val chooseCityRecyclerView: RecyclerView = findViewById(R.id.choose_city_recycler_view)
                    // TODO: Display each coop card.
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
            .map { Pair(it.country, it.city) to it }
            .toMap()
        if (cooperatives.isNullOrEmpty()) {
            Log.w("FETCH.COOPERATIVES", "Successfully got zero cooperatives")
        } else {
            Log.i("FETCH.COOPERATIVES", "Successfully got list of cooperatives")
            Log.v(
                "FETCH.COOPERATIVES",
                cooperatives?.entries?.map { it.value.name }?.toString().orEmpty()
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