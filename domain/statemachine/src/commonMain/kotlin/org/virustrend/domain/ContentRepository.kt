package org.virustrend.domain

import org.virustrend.Country
import org.virustrend.country
import org.virustrend.domain.ContentQuery.GetAllWorldData
import org.virustrend.network.VirusTrendClient

suspend fun Content.Companion.query(query: ContentQuery): Content =
    fetchContent(query)

sealed class ContentQuery {
    object GetAllWorldData : ContentQuery()
}

private suspend fun fetchContent(
    query: ContentQuery,
    client: VirusTrendClient = VirusTrendClient()
): Content = when (query) {
    GetAllWorldData -> with(client.total()) {
        Content(
            selectedCountry = SelectableCountry.None,
            countries = countryCases.mapNotNull { it.country }.asSelectableCountries(),
            casesByCountry = countryCases,
            total = this
        )
    }
}

private fun List<Country>.asSelectableCountries(): List<SelectableCountry> =
    listOf(SelectableCountry.None) + map { SelectableCountry.Some(it) }