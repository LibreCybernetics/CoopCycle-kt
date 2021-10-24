package dev.librecybernetics.types

import dev.librecybernetics.coopcycle.types.CooperativeName
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.ShouldSpec

object CooperativeNameSpec : ShouldSpec({
	context("validation") {
		context("ok") {
			should("Create example") {
				CooperativeName("Twe Wheel Cooperative")
			}
		}

		context("error") {
			should("empty") {
				shouldThrowExactly<IllegalArgumentException> { CooperativeName("") }
			}

			should("blank") {
				shouldThrowExactly<IllegalArgumentException> { CooperativeName("  ") }
			}

			should("trimable") {
				shouldThrowExactly<IllegalArgumentException> { CooperativeName(" Two Wheel Collective ") }
			}
		}
	}
})