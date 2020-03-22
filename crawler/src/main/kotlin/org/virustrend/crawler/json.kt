package org.virustrend.crawler

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

internal val prettyJson: Json =
    Json(configuration = JsonConfiguration.Stable.copy(prettyPrint = true))

internal val defaultJson: Json =
    Json(configuration = JsonConfiguration.Stable)
