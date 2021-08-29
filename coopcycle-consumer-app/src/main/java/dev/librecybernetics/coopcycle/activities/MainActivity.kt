package dev.librecybernetics.coopcycle.activities

import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import dev.librecybernetics.location.LocationActivityService

class MainActivity : AppCompatActivity(), LocationActivityService {
    override val activity: ComponentActivity = this
    override lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { 
            Scaffold() {
                
            }
            Text("MainActivity") }
    }
}