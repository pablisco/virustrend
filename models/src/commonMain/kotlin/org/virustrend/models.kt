package org.virustrend

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import kotlinx.serialization.Serializable

@Serializable
data class GlobalInfo(
    val dataPoint: Cases,
    val updateTimeUnix: Double
) {

    companion object {
        operator fun invoke(
            dataPoint: Cases,
            updateTime: DateTime = DateTime.now()
        ) = GlobalInfo(dataPoint = dataPoint, updateTimeUnix = updateTime.unixMillis)
    }

    val updateTime: DateTime get() = DateTime(updateTimeUnix)

}

@Serializable
data class CasesByDayByCountry(
    val countryName: String,
    val days: List<CaseByDay>
)

@Serializable
data class CaseByDay(
    val dayUnix: Int,
    val cases: Cases,
    val delta: Delta
) {

    companion object {
        operator fun invoke(
            day: Date,
            cases: Cases,
            delta: Delta
        ) = CaseByDay(dayUnix = day.encoded, cases = cases, delta = delta)
    }

    val day: Date get() = Date(dayUnix)

}

@Serializable
data class Cases(
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int,
    val active: Int
)

@Serializable
data class Delta(
    val deltaConfirmed: Int,
    val deltaRecovered: Float
)

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
data class StateCase(
    val location: Location,
    val dataPoint: Cases
)

@Serializable
data class CountryCase(
    val location: Location,
    val dataPoint: Cases
)

@Serializable
data class Country(
    val name: String,
    val slug: String
) {

    constructor(name: String) : this(name, name.urlEncode())

}

operator fun Cases.plus(other: Cases): Cases =
    copy(
        confirmed = confirmed + other.confirmed,
        deaths = deaths + other.deaths,
        recovered = recovered + other.recovered,
        active = active + other.active
    )

private val nonAlphanumericRegex = Regex("[^A-Za-z0-9]")

fun String.urlEncode(): String =
    toLowerCase().replace(nonAlphanumericRegex, "-")