plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {

    defaultConfig {
        applicationId = "org.virustrend.android"
        minSdkVersion(21)
        targetSdkVersion(29)
        compileSdkVersion(29)
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["debug"]

            postprocessing {
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isObfuscate = true
                isOptimizeCode = true
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        main.java.srcDirs("src/main/kotlin")
        test.java.srcDirs("src/test/kotlin")
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

}

dependencies {

    implementation(project(":client"))
    implementation(libraries.androidX.appcompat)
    implementation(libraries.androidX.core)
    implementation(libraries.androidX.constraintLayout)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5")
    implementation("com.pixplicity.sharp:library:1.1.2")

}
