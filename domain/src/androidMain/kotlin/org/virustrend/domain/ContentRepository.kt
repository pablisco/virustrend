package org.virustrend.domain

import org.virustrend.country
import org.virustrend.domain.ContentRepository.Query.GetAllWorldData

object ContentRepository {

    suspend fun query(query: Query): Content =
        fetchContent(query)

    sealed class Query {
        object GetAllWorldData : Query()
    }

}

private suspend fun fetchContent(
    query: ContentRepository.Query,
    client: VirusTrendClient = VirusTrendClient()
): Content = when (query) {
    GetAllWorldData -> with(client.total()) {
        Content(
            countries = countryCases.mapNotNull { it.country },
            casesByCountry = countryCases,
            total = this
        )
    }
}