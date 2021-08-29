package dev.librecybernetics.coopcycle.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import dev.librecybernetics.coopcycle.R

@Composable
@Preview
fun WelcomeScreen(
    onClick: () -> Unit = { }
) {
    val paddingElements = Modifier.padding(10.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        Image(
            painterResource(R.drawable.home_bg),
            stringResource(R.string.home_bg_description),
            Modifier
                .fillMaxWidth()
                .weight(0.5f),
            Alignment.TopCenter
        )

        Spacer(Modifier.weight(0.5f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.ic_coopcycle_logo),
                "Logo",
                Modifier.height(60.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "CoopCycle",
                fontSize = 34.sp,
                fontFamily = FontFamily(listOf(Font(R.font.raleway))),
                fontWeight = FontWeight(500)
            )
            Spacer(Modifier.width(10.dp))
        }

        Spacer(Modifier.weight(0.5f))

        Text(
            stringResource(R.string.welcome),
            paddingElements,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )
        Text(
            stringResource(R.string.welcome_copy),
            paddingElements,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onClick, paddingElements) {
            Text(stringResource(R.string.select_city_prompt), textAlign = TextAlign.Center)
        }
        Spacer(Modifier.weight(0.5f))
    }
}