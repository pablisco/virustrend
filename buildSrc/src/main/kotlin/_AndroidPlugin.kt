import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectProvider

val <T> NamedDomainObjectCollection<T>.release: NamedDomainObjectProvider<T>
    get() = named("release")

val <T> NamedDomainObjectCollection<T>.main: T
    get() = named("main").get()

val <T> NamedDomainObjectCollection<T>.test: T
    get() = named("test").get()
