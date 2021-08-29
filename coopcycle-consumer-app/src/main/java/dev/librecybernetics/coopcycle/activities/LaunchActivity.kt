package dev.librecybernetics.coopcycle.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.material.*
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import dev.librecybernetics.coopcycle.util.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
class LaunchActivity : ComponentActivity() {
    companion object {
        val configuredServerKey = stringPreferencesKey("configured_server")
    }

    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val configuredServer = preferencesDataStore.data.map {
                try {
                    it[configuredServerKey]
                } catch (_: Throwable) {
                    null
                }
            }.firstOrNull()

            when (configuredServer) {
                null ->
                    startActivity(Intent(context, InitialSetup::class.java))
                configuredServer ->
                    startActivity(Intent(context, MainActivity::class.java))
            }
        }
    }
}