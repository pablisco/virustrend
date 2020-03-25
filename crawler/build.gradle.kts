plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.kotlin
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":models", configuration = "jvmDefault"))
    implementation(libraries.csv)
    implementation(libraries.kotlinX.serializable)
}

tasks.create<JavaExec>("crawlConvidRepo") {
    main = "org.virustrend.crawler.MainKt"
    classpath = sourceSets.main.runtimeClasspath
    args = listOf(rootProject.buildDir.absolutePath)
}
