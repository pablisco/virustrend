import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jvmVersion = JavaVersion.VERSION_1_8

fun Project.setupKotlin() {
    tasks.withType<KotlinCompile>().all {
        kotlinOptions.jvmTarget = "$jvmVersion"
    }
}

fun Project.applyKotlin(module: String) {
    apply(plugin = "org.jetbrains.kotlin.$module")
}