package org.virustrend.domain

sealed class Async<out T>(val maybeData: T? = null) {
    sealed class Loading<T>(maybeData: T? = null) : Async<T>(maybeData) {
        object Fresh : Loading<Nothing>()
        data class WithData<T>(val data: T) : Loading<T>(data)
    }

    data class Idle<T>(val data: T) : Async<T>(data)
    sealed class Failed<T>(maybeData: T? = null) : Async<T>(maybeData) {

        abstract val error : Exception

        data class OnLoad(override val error: Exception) : Failed<Nothing>()
        data class OnReload<T>(override val error: Exception, val data: T) : Failed<T>(data)
    }

    fun <R> map(transform: (T) -> R): Async<R> = when(this) {
        is Loading.Fresh -> Loading.Fresh
        is Loading.WithData -> Loading.WithData(transform(data))
        is Idle -> Idle(transform(data))
        is Failed.OnLoad -> Failed.OnLoad(error)
        is Failed.OnReload -> Failed.OnReload(error, transform(data))
    }

}

fun <T, R> Async<T>.maybeData(block: T.() -> R): R? =
    maybeData?.block()