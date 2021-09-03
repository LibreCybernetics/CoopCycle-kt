package dev.librecybernetics.coopcycle.activities

import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.*
import androidx.core.app.ActivityCompat
import dev.librecybernetics.coopcycle.R
import dev.librecybernetics.location.LocationActivityService

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity(), LocationActivityService {
    override val activity: ComponentActivity = this
    override lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        backgroundColor = Color(ActivityCompat.getColor(this, R.color.coopcycle_red))
                    ) {
                            Icon(Icons.Default.Menu, "Side Menu", tint = MaterialTheme.colors.surface)
                    }
                },
                drawerContent = {
                    Column {
                        Text("Drawer")
                    }
                }
            ) {
                Text("MainActivity")
            }
        }
    }
}