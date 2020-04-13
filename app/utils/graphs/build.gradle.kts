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
                implementation(kotlin("stdlib"))
            }
        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
            }
        }
    }
}