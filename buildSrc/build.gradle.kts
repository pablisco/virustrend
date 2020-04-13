repositories {
    google()
    jcenter()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation("com.android.tools.build:gradle:3.6.2")
    implementation(kotlin("gradle-plugin", version = "1.3.70"))
}