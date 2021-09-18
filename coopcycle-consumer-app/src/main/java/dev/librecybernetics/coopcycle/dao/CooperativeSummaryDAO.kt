package dev.librecybernetics.coopcycle.dao

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.util.UTF8StringRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

interface CooperativeSummaryDAO {
	companion object {
		private sealed interface State
		private object Success : State
		private object Updating : State
		private data class Error(val error: VolleyError) : State

		private val state: MutableStateFlow<State?> = MutableStateFlow(null)
		private val mCooperatives: MutableStateFlow<Set<Cooperative>> = MutableStateFlow(setOf())
	}

	val requestQueue: RequestQueue
	val activity: ComponentActivity

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
			{ activity.lifecycleScope.launch { processResponse(it) } },
			{ activity.lifecycleScope.launch { processError(it) } }
		))
		requestQueue.start()
	}

	fun fetchCooperatives(forceUpdate: Boolean = false) {
		if (forceUpdate) pFetchCooperatives() else pFetchCooperativesIfRequired()
	}
}