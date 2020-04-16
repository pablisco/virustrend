package org.virustrend.network

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.url
import org.virustrend.GlobalTotal
import org.virustrend.json.defaultJson
import org.virustrend.query.Query

suspend fun <T : Any> fetch(query: Query<T>): T = internalFetch(query)

@Suppress("UNCHECKED_CAST")
internal suspend fun <T : Any> internalFetch(
    query: Query<T>,
    httpClient: HttpClient = defaultClient,
    baseUrl: String = "https://virustrend.org/api"
): T = when (query) {
    Query.AllCountries -> httpClient.fetchAllWorld(baseUrl)
} as T

private suspend fun HttpClient.fetchAllWorld(baseUrl: String): GlobalTotal =
    get<String> {
        url("$baseUrl/total.json")
    }.let {
        defaultJson.parse(GlobalTotal.serializer(), it)
    }

private val defaultClient by lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = defaultJson)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
        }
    }
}
