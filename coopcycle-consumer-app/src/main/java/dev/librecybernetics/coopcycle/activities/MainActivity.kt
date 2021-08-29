package dev.librecybernetics.coopcycle.activities

import android.app.Activity
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import dev.librecybernetics.location.LocationActivityService

class MainActivity : AppCompatActivity(), LocationActivityService {
    override val activity: Activity = this
    override lateinit var locationManager: LocationManager
}