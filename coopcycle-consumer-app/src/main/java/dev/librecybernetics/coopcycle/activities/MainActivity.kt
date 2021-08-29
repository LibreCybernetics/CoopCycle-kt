package dev.librecybernetics.coopcycle.activities

import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import dev.librecybernetics.location.LocationActivityService

class MainActivity : AppCompatActivity(), LocationActivityService {
    override val activity: ComponentActivity = this
    override lateinit var locationManager: LocationManager
}