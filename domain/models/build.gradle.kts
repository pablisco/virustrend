plugins {
//    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

//android {
//
//    defaultConfig {
//        minSdkVersion(21)
//        targetSdkVersion(29)
//        compileSdkVersion(29)
//        versionCode = 1
//        versionName = "1.0.0"
//    }
//
//    packagingOptions {
//        exclude("META-INF/*.kotlin_module")
//    }
//
//    sourceSets {
//        main.java.srcDirs("src/androidMain/kotlin")
//        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    }
//}


kotlin {
//    android()
    jvm()
    js {
        browser {}
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libraries.kotlinX.serializable.common)
                api(libraries.klock)
            }
        }
        named("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libraries.kotlinX.serializable.jvm)
            }
        }
//        named("androidMain") {
//            dependencies {
//                implementation(kotlin("stdlib-jdk8"))
//                implementation(libraries.kotlinX.serializable.jvm)
//            }
//        }
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.kotlinX.serializable.js)
            }
        }
    }
}