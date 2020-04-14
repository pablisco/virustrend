plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}


kotlin {
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
        named("jsMain") {
            dependencies {
                implementation(kotlin ("stdlib-js"))
                implementation(libraries.kotlinX.serializable.js)
            }
        }
    }
}