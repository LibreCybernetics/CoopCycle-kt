package dev.librecybernetics.coopcycle.dao

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.util.UTF8StringRequest

interface CooperativeSummaryDAO {
    companion object {
        sealed interface Result
        data class Success(val cooperatives: Set<Cooperative>) : Result
        data class Error(val error: VolleyError) : Result

        var result: Result? = null
        private val cooperativeSummaryRequest: StringRequest = UTF8StringRequest(
            Request.Method.GET,
            "https://coopcycle.org/coopcycle.json",
            { processResponse(it) },
            { processError(it) }
        )

        private fun processError(error: VolleyError) {
            result = Error(error)
            Log.e("FETCH.COOPERATIVES", error.message.orEmpty())
        }

        private fun processResponse(response: String) {
            result = Success(Cooperative.setFromJSONString(response))
        }
    }

    val requestQueue: RequestQueue
    val activity: Activity

    private fun sendRequest() {
        requestQueue.add(cooperativeSummaryRequest)
        requestQueue.start()
    }

    fun fetchCooperatives(): Set<Cooperative>? {
        return when (result) {
            is Error -> {
                val error = (result as Error).error
                Toast.makeText(activity, error.localizedMessage, Toast.LENGTH_LONG).show()
                sendRequest()
                null
            }
            is Success -> {
                val cooperatives = (result as Success).cooperatives
                if (cooperatives.isEmpty()) {
                    Log.w("FETCH.COOPERATIVES", "Successfully got zero cooperatives")
                    null
                } else {
                    Log.i("FETCH.COOPERATIVES", "Successfully got list of cooperatives")
                    Log.v("FETCH.COOPERATIVES", cooperatives.map { it.name }.toString())
                    cooperatives
                }
            }
            null -> {
                sendRequest(); null
            }
        }
    }
}