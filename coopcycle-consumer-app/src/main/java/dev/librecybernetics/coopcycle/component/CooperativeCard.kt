package dev.librecybernetics.coopcycle.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
    onClick: () -> Unit = { }
) {
    Card(onClick = { onClick() }) {
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