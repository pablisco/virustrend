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
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.kotlinX.javascript.css)
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