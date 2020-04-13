plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

androidLibrary()

kotlin {
    js {
        browser {}
    }
    android()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":domain:models"))
                implementation(kotlin("stdlib"))
            }
        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.kotlinX.javascript.css)
                implementation("org.jetbrains:kotlin-react:16.13.0-pre.94-kotlin-1.3.70")
//                implementation("org.jetbrains:kotlin-react-dom:16.13.0-pre.94-kotlin-1.3.70")
//                implementation(npm("react", "16.13.0"))
//                implementation(npm("react-dom", "16.13.0"))
            }
        }
        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libraries.androidX.core)
            }
        }
    }
}