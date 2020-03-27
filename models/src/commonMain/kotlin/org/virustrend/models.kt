package org.virustrend

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class GlobalTotal(
    val cases: Cases,
    val countryCases: List<CountryCases>,
    val updateTimeUnix: Double
) {
    companion object {
        operator fun invoke(
            cases: Cases,
            countryCases: List<CountryCases>,
            updateTime: DateTime = DateTime.now()
        ) = GlobalTotal(cases, countryCases, updateTime.unixMillis)
    }
}

@Serializable
data class CasesByDayByCountry(val countryName: String, val days: List<CaseByDay>)

val CasesByDayByCountry.country: Country? get() = Country(countryName)

@Serializable
data class CaseByDay(val dayUnix: Int, val cases: Cases, val delta: Delta) {
    companion object {
        operator fun invoke(day: Date, cases: Cases, delta: Delta) =
            CaseByDay(dayUnix = day.encoded, cases = cases, delta = delta)
    }
}

val CaseByDay.day: Date get() = Date(dayUnix)

@Serializable
data class Cases(
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int,
    val active: Int
)

@Serializable
data class Delta(val deltaConfirmed: Int, val deltaRecovered: Float)

@Serializable
data class CasesByLocation(
    val combinedKey: String,
    val location: Location,
    val dataPoint: Cases
)

@Serializable
data class Location(
    /**
     * Federal Information Processing Standards
     */
    val fips: String,
    val provinceOrState: String,
    val countryName: String,
    val latitude: Float,
    val longitude: Float
)

@Serializable
data class CountryCases(val countryName: String?, val cases: Cases)

val CountryCases.country: Country? get() = countryName?.let { Country(it) }

operator fun Cases.plus(other: Cases): Cases =
    copy(
        confirmed = confirmed + other.confirmed,
        deaths = deaths + other.deaths,
        recovered = recovered + other.recovered,
        active = active + other.active
    )