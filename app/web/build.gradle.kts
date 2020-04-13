plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(project(":domain:statemachine"))
    implementation(project(":app:utils:color"))
    implementation(npm("text-encoding"))
    implementation(npm("abort-controller"))
    implementation(npm("utf-8-validate"))
    implementation(npm("fs"))
    implementation(npm("bufferutil"))

    implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
    implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
    implementation(npm("react", "16.13.0"))
    implementation(npm("react-dom", "16.13.0"))

    implementation(libraries.kotlinX.javascript.styled)
    implementation(npm("styled-components"))
    implementation(npm("inline-style-prefixer"))
//    implementation(npm("material-components-web-react"))

    implementation(libraries.kotlinX.coroutines.core)
    implementation(libraries.kotlinX.coroutines.js)
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
    implementation("com.ccfraser.muirwik:muirwik-components:0.4.1")
    implementation(npm("@material-ui/core"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
}

kotlin {
    target {
        @Suppress("EXPERIMENTAL_API_USAGE")
        browser {
            // https://kotlinlang.org/docs/reference/javascript-dce.html#known-issue-dce-and-ktor
            dceTask {
                keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
            }
//            webpackTask {
//                output.libraryTarget = COMMONJS
//            }
            distribution {
                directory = rootProject.buildDir.resolve("pages")
            }
        }
        useCommonJs()
    }

    sourceSets {
        main.resources.srcDir("../sharedAssets")
    }
}
