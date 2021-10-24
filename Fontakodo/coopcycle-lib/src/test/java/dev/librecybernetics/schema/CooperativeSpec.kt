package dev.librecybernetics.schema

import dev.librecybernetics.coopcycle.schema.Cooperative
import dev.librecybernetics.coopcycle.types.*
import dev.librecybernetics.types.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlinx.serialization.ExperimentalSerializationApi
import java.math.BigDecimal

@ExperimentalSerializationApi
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

			should("serializes example") {
				val expected =
					"""[{"name":"Two Wheel Collective","mail":"twcbicimensajeria@gmail.com",""" +
									""""city":"Ciudad de México","country":"MX","coopcycle_url":""" +
									""""https://twc.coopcycle.org","latitude":"19.43268","longitude":""" +
									""""-99.13421"}]"""

				Cooperative.toJSONString(setOf(coop1)) shouldBe expected
			}

			should("deserializes example") {
				val example = javaClass.getResource("/fixtures/cooperatives.json")?.readText()!!
				val parsed = Cooperative.setFromJSONString(example)

				parsed shouldHaveSize 40
				parsed shouldContain coop1
				parsed shouldNot exist { it.name == CooperativeName("Orléans Cycloposteurs") }
			}
		}
	}
})