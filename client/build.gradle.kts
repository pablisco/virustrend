plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.kotlin
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
                implementation("io.ktor:ktor-client-core:1.3.1")
                implementation("io.ktor:ktor-client-serialization:1.3.1")
                api(project(":models"))
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
                implementation("io.ktor:ktor-client-core:1.3.1")
                implementation("io.ktor:ktor-client-serialization-jvm:1.3.1")
                implementation("io.ktor:ktor-client-okhttp:1.3.1")
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
                implementation("io.ktor:ktor-client-android:1.3.1")
                implementation("io.ktor:ktor-client-serialization-jvm:1.3.1")
                implementation("io.ktor:ktor-client-okhttp:1.3.1")
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
                implementation("io.ktor:ktor-client-js:1.3.1")
                implementation("io.ktor:ktor-client-serialization-js:1.3.1")
            }
        }
        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}