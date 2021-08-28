package dev.librecybernetics.coopcycle.dao

import android.app.Activity
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.util.UTF8StringRequest
import kotlinx.coroutines.flow.*

interface CooperativeSummaryDAO {
    companion object {
        private sealed interface State
        private object Success : State
        private object Updating : State
        private data class Error(val error: VolleyError) : State

        private var state: MutableStateFlow<State?> = MutableStateFlow(null)
        private val mCooperatives: MutableStateFlow<Set<Cooperative>> = MutableStateFlow(setOf())
    }

    val requestQueue: RequestQueue
    val activity: Activity

    val cooperatives: StateFlow<Set<Cooperative>>
        get() {
            pFetchCooperativesIfRequired()
            return mCooperatives
        }

    private fun pFetchCooperativesIfRequired() {
        state.update {
            when (it) {
                null -> {
                    pFetchCooperatives()
                    Updating
                }
                Updating -> Updating
                is Error -> {
                    pFetchCooperatives()
                    Updating
                }
                Success -> Success
            }
        }
    }

    private fun pFetchCooperatives() {
        fun processError(error: VolleyError) {
            state.value = Error(error)
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
            mCooperatives.update { current -> current + newData }
            state.value = Success
        }

        requestQueue.add(UTF8StringRequest(
            Request.Method.GET,
            "https://coopcycle.org/coopcycle.json",
            { processResponse(it) },
            { processError(it) }
        ))
        requestQueue.start()
    }

    fun fetchCooperatives(forceUpdate: Boolean = false) {
        if (forceUpdate) pFetchCooperatives() else pFetchCooperativesIfRequired()
    }
}