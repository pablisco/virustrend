plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":domain:network"))
    implementation(npm("text-encoding"))
    implementation(npm("abort-controller"))
    implementation(npm("utf-8-validate"))
    implementation(npm("fs"))
    implementation(npm("bufferutil"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}

kotlin {
    target {
        browser {
            // https://kotlinlang.org/docs/reference/javascript-dce.html#known-issue-dce-and-ktor
            @Suppress("EXPERIMENTAL_API_USAGE")
            dceTask {
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
        }
    }
}