package dev.librecybernetics.util

import kotlinx.serialization.json.Json

val lenientJson: Json = Json { isLenient = true; ignoreUnknownKeys = true }