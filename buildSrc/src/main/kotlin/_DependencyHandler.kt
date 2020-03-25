@file:Suppress("unused") // Used for scoping

import org.gradle.api.artifacts.dsl.DependencyHandler

val DependencyHandler.libraries get() = Libraries

object Libraries {

    val androidX = AndroidX
    val google = Google
    val kotlinX = KotlinX

    const val csv = "com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:1.1.3"
    }

    object Google {
        const val maps = "com.google.android.gms:play-services-maps:16.1.0"
    }

    object KotlinX {
        const val serializable = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0"
    }

}