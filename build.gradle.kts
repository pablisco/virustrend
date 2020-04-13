buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://kotlin.bintray.com/kotlinx")
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath(kotlin("frontend-plugin", version = Versions.kotlinFrontendPlugin))
        classpath(kotlin("serialization", version = Versions.kotlin))
        classpath("com.android.tools.build:gradle:3.6.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://jitpack.io")
        maven("https://dl.bintray.com/korlibs/korlibs")
    }
}