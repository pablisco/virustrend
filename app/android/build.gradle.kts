plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

androidApp(id = "org.virustrend.android") {

    sourceSets {
        main.assets.srcDir("../sharedAssets")
    }

}

dependencies {

    implementation(project(":domain:statemachine"))
    implementation(project(":app:utils:color"))
    implementation(libraries.androidX.appcompat)
    implementation(libraries.androidX.core)
    implementation(libraries.androidX.constraintLayout)
    implementation(libraries.google.material)
    implementation(libraries.androidX.lifecycle)
    implementation(libraries.androidX.viewModel)
    implementation(libraries.androidX.fragment)
    implementation(libraries.kotlinX.coroutines.core)
    implementation(libraries.kotlinX.coroutines.android)
    implementation(libraries.sharp)

}
