package org.virustrend.client

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.url
import org.virustrend.GlobalTotal
import org.virustrend.json.defaultJson

class VirusTrendClient(
    private val baseUrl : String = "https://virustrend.org/api"
) {

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = defaultJson)
        }
    }

    suspend fun total(): GlobalTotal =
        httpClient.get<String> { url("$baseUrl/total.json") }
            .let { defaultJson.parse(GlobalTotal.serializer(), it) }

}
