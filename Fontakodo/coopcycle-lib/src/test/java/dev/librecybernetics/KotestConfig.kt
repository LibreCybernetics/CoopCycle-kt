package dev.librecybernetics

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.Listener
import io.kotest.extensions.junitxml.JunitXmlReporter

class KotestConfig : AbstractProjectConfig() {
	override fun listeners(): List<Listener> = listOf(
		JunitXmlReporter(
			includeContainers = false,
			useTestPathAsName = true
		)
	)
}