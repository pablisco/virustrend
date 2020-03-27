package org.virustrend.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

private val defaultConfiguration: JsonConfiguration =
    JsonConfiguration.Stable.copy(isLenient = true, ignoreUnknownKeys = true)

private val prettyConfiguration =
    defaultConfiguration.copy(prettyPrint = true)

val defaultJson: Json = Json(configuration = defaultConfiguration)

val prettyJson: Json = Json(configuration = prettyConfiguration)
