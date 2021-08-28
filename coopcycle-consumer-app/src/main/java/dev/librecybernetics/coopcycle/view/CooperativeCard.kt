package dev.librecybernetics.coopcycle.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import dev.librecybernetics.coopcycle.types.CooperativeName
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode

@Composable
@ExperimentalMaterialApi
fun CooperativeCard(
    cityName: CityName,
    cooperativeName: CooperativeName,
    countryCode: CountryCode,
    onClick: () -> Unit = { }
) {
    Card(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp, 5.dp)
        ) {
            Column {
                Text(cityName.name)
                Text(cooperativeName.name)
            }
            Spacer(Modifier.weight(1f))
            Text(countryCode.code)
        }
    }
}