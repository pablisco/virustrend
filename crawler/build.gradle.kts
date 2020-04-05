plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":domain:models"))
    implementation(libraries.csv)
    implementation(libraries.kotlinX.serializable.jvm)
}

tasks.create<JavaExec>("crawlConvidRepo") {
    group = "run"
    main = "org.virustrend.crawler.MainKt"
    classpath = sourceSets.main.runtimeClasspath
    args = listOf(rootProject.buildDir.absolutePath)
}
