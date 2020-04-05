plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

android {

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        compileSdkVersion(29)
        versionCode = 1
        versionName = "1.0.0"
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

    sourceSets {
        main.java.srcDirs("src/androidMain/kotlin")
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

kotlin {
    jvm()
    android()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libraries.ktorClient.core)
                implementation(libraries.ktorClient.serialization)
                api(project(":domain:models"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        named("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libraries.ktorClient.core)
                implementation(libraries.ktorClient.serializationJvm)
                implementation(libraries.ktorClient.okhttp)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        named("androidMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libraries.ktorClient.android)
                implementation(libraries.ktorClient.serializationJvm)
                implementation(libraries.ktorClient.okhttp)
            }
        }
        named("androidTest") {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.ktorClient.js)
                implementation(libraries.ktorClient.serializationJs)
//                implementation("io.ktor:ktor-client-js:1.3.1")
//                implementation("io.ktor:ktor-client-serialization-js:1.3.1")
            }
        }
        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}