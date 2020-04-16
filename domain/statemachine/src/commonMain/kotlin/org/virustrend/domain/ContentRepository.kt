package org.virustrend.domain

import org.virustrend.network.fetch
import org.virustrend.query.Query

internal suspend fun <T : Any> query(
    query: Query<T>,
    cache: InMemoryCache = InMemoryCache.default
): T = cache.cached(query) ?: fetch(query).also { cache.save(query, it) }


class InMemoryCache(
    private val keyValues:  MutableMap<Query<*>, Any> = mutableMapOf()
) {

    companion object {
        val default: InMemoryCache = InMemoryCache()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> cached(query: Query<T>) : T? =
        keyValues[query] as? T

    fun <T: Any> save(query: Query<T>, value: Any) {
        keyValues[query] = value
    }

}
