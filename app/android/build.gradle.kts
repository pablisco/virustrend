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
        main.assets.srcDir("../sharedAssets")
        test.java.srcDirs("src/test/kotlin")
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lintOptions {
        isAbortOnError = false
    }

}

dependencies {

    implementation(project(":domain:network"))
    implementation(project(":domain:statemachine"))
    implementation(libraries.androidX.appcompat)
    implementation(libraries.androidX.core)
    implementation(libraries.androidX.constraintLayout)
    implementation(libraries.google.material)
    implementation(libraries.androidX.lifecycle)
    implementation(libraries.androidX.viewModel)
    implementation("androidx.fragment:fragment-ktx:1.2.4")
    implementation(libraries.kotlinX.coroutines.core)
    implementation(libraries.kotlinX.coroutines.android)
    implementation(libraries.sharp)

}
