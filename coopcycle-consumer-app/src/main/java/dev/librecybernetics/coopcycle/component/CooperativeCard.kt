package dev.librecybernetics.coopcycle.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.types.CoopcycleServerAddress
import dev.librecybernetics.coopcycle.types.CooperativeEMailAddress
import dev.librecybernetics.coopcycle.types.CooperativeName
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode
import dev.librecybernetics.types.Latitude
import dev.librecybernetics.types.Longitude
import kotlinx.coroutines.*
import java.math.BigDecimal

private object CooperativePreviewProvider : PreviewParameterProvider<Cooperative> {
    override val values: Sequence<Cooperative> = sequenceOf(
        Cooperative(
            CooperativeName("CooperativeName"),
            CooperativeEMailAddress("contact@email.com"),
            CityName("CityName"),
            CountryCode("CC"),
            CoopcycleServerAddress("demo.coopcycle.org"),
            Latitude(BigDecimal(0)),
            Longitude(BigDecimal(0))
        )
    )
}

@Composable
@ExperimentalMaterialApi
@Preview
fun CooperativeCard(
    @PreviewParameter(provider = CooperativePreviewProvider::class)
    cooperative: Cooperative,
    onClick: () -> Unit = { },
    highlight: Boolean = false
) {
    val highlightScalar = remember { Animatable(0f) }
    if (highlight) LaunchedEffect(true) {
        delay(600)
        highlightScalar.animateTo(1f)
        delay(200)
        highlightScalar.animateTo(0f)
    }

    val bgColor = if (!highlight) MaterialTheme.colors.surface else lerp(
        MaterialTheme.colors.surface,
        MaterialTheme.colors.secondary,
        highlightScalar.value
    )

    Card(
        onClick = { onClick() },
        backgroundColor = bgColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp, 5.dp)
        ) {
            Column {
                Text(
                    cooperative.city.name,
                    fontSize = 18.sp
                )
                Text(cooperative.name.name)
            }
            Spacer(Modifier.weight(1f))
            Text(cooperative.country.code)
        }
    }
}