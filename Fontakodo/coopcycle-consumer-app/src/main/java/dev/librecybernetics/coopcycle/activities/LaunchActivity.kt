package dev.librecybernetics.coopcycle.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import dev.librecybernetics.coopcycle.util.preferojDatumbutiko
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class LaunchActivity : ComponentActivity() {
	companion object {
		val agorditaServiloKlavo = stringPreferencesKey("configured_server")
	}

	private val context = this

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		lifecycleScope.launch {
			val configuredServer = preferojDatumbutiko.data.map {
				try {
					it[agorditaServiloKlavo]
				} catch (_: Throwable) {
					null
				}
			}.firstOrNull()

			when (configuredServer) {
				null ->
					startActivity(Intent(context, KomencaAgordiAktiveco::class.java))
				configuredServer ->
					startActivity(Intent(context, ĈefaAktiveco::class.java))
			}
		}
	}
}