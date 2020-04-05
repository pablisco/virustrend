import org.gradle.api.Project

@Suppress("unused") // Used for scoping
val Project.libraries get() = Libraries

object Libraries {

    val androidX = AndroidX
    val kotlinX = KotlinX
    val google = Google
    val ktorClient = KtorClient

    const val csv = "com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3"
    const val sharp = "com.pixplicity.sharp:library:1.1.2"
    const val klock = "com.soywiz.korlibs.klock:klock:1.10.0"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val core = "androidx.core:core-ktx:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta4"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"
    }

    object Google {
        const val material = "com.google.android.material:material:1.1.0"
    }

    object KotlinX {
        val serializable = Serializable
        val coroutines = Coroutines

        object Coroutines {
            const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.5"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5"
            const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.5"
        }

        object Serializable {
            const val jvm = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0"
            const val common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.20.0"
            const val js = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0"
        }

    }

    private const val ktorVersion = "1.3.2"

    object KtorClient {
        const val core = "io.ktor:ktor-client-core:$ktorVersion"
        const val android = "io.ktor:ktor-client-android:$ktorVersion"
        const val js = "io.ktor:ktor-client-js:$ktorVersion"
        const val serialization = "io.ktor:ktor-client-serialization:$ktorVersion"
        const val serializationJvm = "io.ktor:ktor-client-serialization-jvm:$ktorVersion"
        const val serializationJs = "io.ktor:ktor-client-serialization-js:$ktorVersion"
        const val okhttp = "io.ktor:ktor-client-okhttp:$ktorVersion"

    }

}