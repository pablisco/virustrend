plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")
}

tasks.create<JavaExec>("crawlConvidRepo") {
    main = "org.virustrend.crawler.MainKt"
    classpath = sourceSets.main.get().runtimeClasspath
    args = listOf(rootProject.buildDir.absolutePath)
}
