package dev.librecybernetics.coopcycle.dao

import android.app.Activity
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.util.UTF8StringRequest
import kotlinx.coroutines.flow.*

interface CooperativeSummaryDAO {
    companion object {
        sealed interface Result
        object Success : Result
        data class Error(val error: VolleyError) : Result

        var result: Result? = null
    }

    val cooperatives: MutableStateFlow<Set<Cooperative>>
    val requestQueue: RequestQueue
    val activity: Activity

    fun requestCooperatives() {
        fun processError(error: VolleyError) {
            result = Error(error)
            Log.e("FETCH.COOPERATIVES", error.message.orEmpty())
        }

        fun processResponse(response: String) {
            val newData = Cooperative.setFromJSONString(response)
            if (newData.isEmpty()) {
                Log.w("FETCH.COOPERATIVES", "Successfully got zero cooperatives")
            } else {
                Log.i("FETCH.COOPERATIVES", "Successfully got list of cooperatives")
                Log.v("FETCH.COOPERATIVES", newData.map { it.name }.toString())
            }
            cooperatives.update { current -> current + newData }
            result = Success
        }

        val cooperativeSummaryRequest: StringRequest = UTF8StringRequest(
            Request.Method.GET,
            "https://coopcycle.org/coopcycle.json",
            { processResponse(it) },
            { processError(it) }
        )

        requestQueue.add(cooperativeSummaryRequest)
        requestQueue.start()
    }
}