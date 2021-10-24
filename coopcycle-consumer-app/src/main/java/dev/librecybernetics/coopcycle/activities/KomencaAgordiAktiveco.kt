package dev.librecybernetics.coopcycle.activities

import android.Manifest
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.BuildConfig
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dev.librecybernetics.coopcycle.activities.LaunchActivity.Companion.agorditaServiloKlavo
import dev.librecybernetics.coopcycle.dao.ResumoKooperativoDAO
import dev.librecybernetics.coopcycle.navigation.KomencaAgordiNavigado
import dev.librecybernetics.coopcycle.util.preferojDatumbutiko
import dev.librecybernetics.location.LokaServo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class KomencaAgordiAktiveco : FragmentActivity(), LokaServo, ResumoKooperativoDAO {
	companion object {
		private val krudaLoko: MutableStateFlow<Location?> = MutableStateFlow(null)
	}

	override val aktiveco: ComponentActivity = this
	override lateinit var lokmanaĝero: LocationManager
	override lateinit var petoVeco: RequestQueue

	private fun ĝisdatigiLokon() {
		val loko = akiriLokon(
			LokaServo.Companion.LokoFreŝeco.Iu,
			LokaServo.Companion.LocationAccuracy.Kruda
		)
		krudaLoko.value = loko
		if (BuildConfig.DEBUG) Log.d("KomencaAgordi.Loko", krudaLoko.value.toString())
	}

	override fun onCreate(savitaStato: Bundle?) {
		super.onCreate(savitaStato)

		lokmanaĝero = getSystemService(LocationManager::class.java)
		petoVeco = Volley.newRequestQueue(this)

		setContent {
			KomencaAgordiNavigado(
				kooperativoj = kooperativoj,
				loko = krudaLoko,
				aldonaAgo = { ĝisdatigiLokon() },
				aklakanteKooperativon =
				{ kooperativo ->
					lifecycleScope.launch {
						preferojDatumbutiko.edit { preferoj ->
							preferoj[agorditaServiloKlavo] = kooperativo.coopcycle_url.address
						}
					}
					startActivity(Intent(this, ĈefaAktiveco::class.java))
				}
			)
		}

		lifecycleScope.launch {
			alportuKooperativo() // Alportu antaŭ la datumoj necesas
		}
	}

	override fun onRequestPermissionsResult(petoKodo: Int, permesoj: Array<out String>, donuRezultoj: IntArray) {
		if (petoKodo == 0 && permesoj.contains(Manifest.permission.ACCESS_COARSE_LOCATION)) ĝisdatigiLokon()
		else super.onRequestPermissionsResult(petoKodo, permesoj, donuRezultoj)
	}
}