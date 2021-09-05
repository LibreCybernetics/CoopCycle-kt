package dev.librecybernetics.coopcycle.types

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CooperativeName(val name: String) {
	init {
		require(name.isNotBlank())
		require(name.trim() == name)
	}
}
