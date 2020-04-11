plugins {
    id("com.android.library")
    kotlin("multiplatform")
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