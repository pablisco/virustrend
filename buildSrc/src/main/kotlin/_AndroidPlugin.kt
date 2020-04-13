import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

val <T> NamedDomainObjectCollection<T>.release: NamedDomainObjectProvider<T>
    get() = named("release")

val <T> NamedDomainObjectCollection<T>.main: T
    get() = named("main").get()

val <T> NamedDomainObjectCollection<T>.test: T
    get() = named("test").get()

val <T> NamedDomainObjectCollection<T>.androidMain: T
    get() = named("androidMain").get()

inline fun <reified T : BaseExtension> Project.baseAndroid(crossinline block: T.() -> Unit = {}) {
    configure<T> {
        defaultConfig {
            minSdkVersion(21)
            targetSdkVersion(29)
            compileSdkVersion(29)
            versionCode = 1
            versionName = "1.0.0"
        }
        packagingOptions {
            exclude("META-INF/*.kotlin_module")
        }
        sourceSets {
            main.java.srcDirs("src/androidMain/kotlin")
            main.res.srcDirs("src/androidMain/res")
            main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
        compileOptions {
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }
        block()
    }
    setupKotlin()
}

fun Project.androidLibrary(block: LibraryExtension.() -> Unit = {}) {
    baseAndroid<LibraryExtension> {
        block()
    }
}

fun Project.androidApp(id: String, block: AppExtension.() -> Unit = {}) {
    baseAndroid<AppExtension> {
        defaultConfig {
            applicationId = id
        }
        buildTypes {
            release {
                signingConfig = signingConfigs["debug"]
                postprocessing {
                    isRemoveUnusedCode = true
                    isRemoveUnusedResources = true
                    isObfuscate = true
                    isOptimizeCode = true
                }
            }
        }
        block()
    }
}