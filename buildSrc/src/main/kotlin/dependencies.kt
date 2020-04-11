@file:Suppress("ClassName") // lowercase usage for dsl

object libraries {

    const val csv = "com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3"
    const val sharp = "com.pixplicity.sharp:library:1.1.2"
    const val klock = "com.soywiz.korlibs.klock:klock:1.10.0"

    object androidX {
        const val appcompat = "androidx.appcompat:appcompat:1.1.0"
        const val core = "androidx.core:core-ktx:1.2.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta4"
        const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"
    }

    object google {
        const val material = "com.google.android.material:material:1.1.0"
    }

    object kotlinX {

        object javascript {
            private const val version = "1.0.0-pre.94-kotlin-1.3.70"
            const val css = "org.jetbrains:kotlin-css-js:$version"
            const val styled = "org.jetbrains:kotlin-styled:$version"
        }

        object coroutines {
            private const val version = "1.3.5"
            const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
        }

        object serializable {
            private const val version = "0.20.0"
            const val jvm = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$version"
            const val common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$version"
            const val js = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$version"
        }

    }


    object ktorClient {
        private const val version = "1.3.2"
        const val core = "io.ktor:ktor-client-core:$version"
        const val android = "io.ktor:ktor-client-android:$version"
        const val js = "io.ktor:ktor-client-js:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
        const val serializationJvm = "io.ktor:ktor-client-serialization-jvm:$version"
        const val serializationJs = "io.ktor:ktor-client-serialization-js:$version"
        const val okhttp = "io.ktor:ktor-client-okhttp:$version"

    }

}