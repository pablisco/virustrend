rootProject.name = "virustrend"

enableFeaturePreview("GRADLE_METADATA")

include(":domain:statemachine")
include(":domain:network")
include(":domain:models")
include(":crawler")
include(":app:android")
include(":app:web")
include(":app:utils:color")
include(":app:utils:graphs")