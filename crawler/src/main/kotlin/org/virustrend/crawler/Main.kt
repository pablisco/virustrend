package org.virustrend.crawler

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate

fun main(args: Array<String>) {
    val workingDirectory = args.firstOrNull() ?: error("missing working directory")
    val target = Paths.get(workingDirectory).resolve("../build/pages/api")
    Files.createDirectories(target)
    val timeCasesCsv = timeCasesUrl.readCsv()
    val dayInfoMap = timeCasesCsv.asCountryTimeCases()
    dayInfoMap.toJson(prettyJson).saveTo(output = target.resolve("timeCases.pretty.json"))
    dayInfoMap.toJson(defaultJson).saveTo(output = target.resolve("timeCases.json"))
}

private fun List<CountryTimeCases>.toJson(json: Json) =
    json.stringify(ListSerializer(CountryTimeCases.serializer()), this)

private fun List<Row>.asCountryTimeCases(): List<CountryTimeCases> =
    map { it.toTimeCase() }
        .groupBy({ it.first }, { it.second })
        .map { (countryName, days) ->
            CountryTimeCases(countryName = countryName, days = days)
        }

private fun Row.toTimeCase() =
    string("Country_Region") to TimeCase(
        day = localDate("Last_Update") ?: LocalDate.MIN,
        dataPoint = DataPoint(
            confirmed = int("Confirmed") ?: 0,
            deaths = int("Deaths") ?: 0,
            recovered = int("Recovered") ?: 0,
            active = int("Active") ?: 0,
            deltaConfirmed = int("Delta_Confirmed") ?: 0,
            deltaRecovered = float("Delta_Recovered") ?: 0f
        )
    )

private fun String.saveTo(output: Path) {
    toByteArray().let { Files.write(output, it) }
}

@Serializable
data class CountryTimeCases(
    val countryName: String,
    val days: List<TimeCase>
)

@Serializable
data class TimeCase(
    @Serializable(with = LocalDateSerializer::class) val day: LocalDate,
    val dataPoint: DataPoint
)

@Serializable
data class DataPoint(
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int,
    val active: Int,
    val deltaConfirmed: Int,
    val deltaRecovered: Float
)

private const val convidRepo = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19"
private val timeCasesUrl = URL("$convidRepo/web-data/data/cases_time.csv")

