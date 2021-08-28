package dev.librecybernetics.coopcycle.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import dev.librecybernetics.coopcycle.R

@Composable
fun WelcomeScreen(
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource(R.drawable.home_bg),
            stringResource(R.string.home_bg_description),
        )
        Text(stringResource(R.string.welcome), textAlign = TextAlign.Center, fontSize = 24.sp)
        Text(stringResource(R.string.welcome_copy), textAlign = TextAlign.Center)
        Button(onClick = onClick) {
            Text(stringResource(R.string.choose_city_button))
        }
    }
}