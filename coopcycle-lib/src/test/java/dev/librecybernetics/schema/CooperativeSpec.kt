package dev.librecybernetics.schema

import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.types.CoopcycleServerAddress
import dev.librecybernetics.coopcycle.types.CooperativeEMailAddress
import dev.librecybernetics.coopcycle.types.CooperativeName
import dev.librecybernetics.types.CityName
import dev.librecybernetics.types.CountryCode
import dev.librecybernetics.types.Latitude
import dev.librecybernetics.types.Longitude
import io.kotest.core.spec.style.ShouldSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.math.BigDecimal

object CooperativeSpec : ShouldSpec({
    context("serializers") {
        context("json") {
            val coop1 = Cooperative(
                CooperativeName("Two Wheel Collective"),
                CooperativeEMailAddress("twcbicimensajeria@gmail.com"),
                CityName(name = "Ciudad de México"),
                CountryCode("MX"),
                CoopcycleServerAddress("https://twc.coopcycle.org"),
                Latitude(BigDecimal("19.43268")),
                Longitude(BigDecimal("-99.13421"))
            )
            val coop2 = Cooperative(
                CooperativeName("Orléans Cycloposteurs"),
                CooperativeEMailAddress("lescycloposteurs@mailo.com"),
                CityName("Orléans"),
                CountryCode("FR"),
                null,
                Latitude(BigDecimal("47.9040718")),
                Longitude(BigDecimal("1.8858347"))
            )
            should("serializes example") {
                val example = Cooperative.toJSONString(setOf(coop1, coop2))
                print(example)
            }
            should("deserializes example") {
                val example = javaClass.getResource("/fixtures/cooperatives.json").readText()
                val parsed = Cooperative.setFromJSONString(example)

                assertEquals(parsed.size, 68)
                assertTrue(parsed.contains(coop1))
                assertTrue(parsed.contains(coop2))
            }
        }
    }
})