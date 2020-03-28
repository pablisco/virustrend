@file:Suppress("unused") // Used for scoping

import org.gradle.api.artifacts.dsl.DependencyHandler

val DependencyHandler.libraries get() = Libraries

object Libraries {

    val androidX = AndroidX
    val kotlinX = KotlinX
    val google = Google
    val ktorClient = KtorClient

    const val csv = "com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3"
    const val sharp = "com.pixplicity.sharp:library:1.1.2"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val core = "androidx.core:core-ktx:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta4"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
    }

    object Google {
        const val material = "com.google.android.material:material:1.1.0"
    }

    object KotlinX {
        const val serializable = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0"
        val coroutines = Coroutines

        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5"
        }
    }

    object KtorClient {
        const val android = "io.ktor:ktor-client-android:1.3.1"
        const val serializationJvm = "io.ktor:ktor-client-serialization-jvm:1.3.1"
        const val okhttp = "io.ktor:ktor-client-okhttp:1.3.1"

    }

}