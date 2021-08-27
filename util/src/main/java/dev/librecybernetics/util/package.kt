package dev.librecybernetics.util

import kotlinx.serialization.json.Json

val lenientJson = Json { isLenient = true; ignoreUnknownKeys = true }