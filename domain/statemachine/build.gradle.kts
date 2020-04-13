plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

androidLibrary()

kotlin {
    android()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libraries.kotlinX.coroutines.common)
                api(project(":domain:network"))
                api(project(":domain:models"))
            }
        }
//        commonTest {
//            dependencies {
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
//            }
//        }
        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libraries.kotlinX.coroutines.core)
                implementation(libraries.kotlinX.coroutines.android)
            }
        }
//        named("androidTest") {
//            dependencies {
//                implementation(kotlin("test-junit"))
//            }
//        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.kotlinX.coroutines.core)
                implementation(libraries.kotlinX.coroutines.js)
            }
        }
//        named("jsTest") {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
    }
}