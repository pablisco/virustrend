package org.virustrend.query

import org.virustrend.GlobalTotal
import kotlin.reflect.KClass

sealed class Query<R : Any>(val returnType: KClass<R>) {
    object AllCountries : Query<GlobalTotal>(GlobalTotal::class)
}