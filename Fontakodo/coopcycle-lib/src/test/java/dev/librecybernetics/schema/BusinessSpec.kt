package dev.librecybernetics.schema

import dev.librecybernetics.coopcycle.schema.*
import dev.librecybernetics.coopcycle.types.BusinessName
import dev.librecybernetics.types.*
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.maps.shouldContain
import kotlinx.serialization.ExperimentalSerializationApi
import java.math.BigDecimal

@ExperimentalSerializationApi
object BusinessSpec : ShouldSpec({
	context("serializers") {
		context("json") {
			val business16 = Business(
				BusinessId(16),
				BusinessName("Vendaval Cooperativa Panadera y Algo Mas"),
				enabled = true,
				Address(
					name = "Vendaval Cooperativa Panadera y Algo Mas",
					streetAddress = "Calle Guillermo Prieto 46, Cuauhtémoc, Ciudad de México, CDMX, México",
					Telephone("+525554180054"),
					Geolocation(
						Latitude(BigDecimal("19.4373957")),
						Longitude(BigDecimal("-99.162632"))
					)
				)
			)
			should("deserializes example") {
				val example = javaClass.getResource("/fixtures/api/restaurants.json")?.readText()!!
				val parsed = Business.mapFromJSONString(example)

				parsed shouldContain (BusinessId(16) to business16)
			}
		}
	}
})