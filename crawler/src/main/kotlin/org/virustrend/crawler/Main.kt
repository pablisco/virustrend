package org.virustrend.crawler

import com.soywiz.klock.Date
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.virustrend.*
import org.virustrend.json.defaultJson
import org.virustrend.json.prettyJson
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
    val workingDirectory = args.firstOrNull() ?: error("missing working directory")
    val target = Paths.get(workingDirectory).resolve("../build/pages/api")
    val cache = Paths.get(workingDirectory).resolve("../build/pages/cache")
    val (casesByTimeRows, _, _, casesByCountryRows) = Csv.values().map { it.read(cache) }

    casesByTimeRows.asCasesByDayByCountry().also { daily ->
        saveJson(target, "daily") {
            stringify(daily)
        }

        daily.forEach { casesByDay ->
            casesByDay.country?.apply {
                saveJson(target.resolve("daily"), slug) {
                    stringify(casesByDay)
                }
            }

        }
    }

    casesByCountryRows.map { it.countryCases }.also { countryCases: List<CountryCases> ->
        saveJson(target, "total") {
            stringify(GlobalTotal(
                cases = countryCases.map { it.cases }.reduce { current, next -> current + next },
                countryCases = countryCases
            ))
        }
        countryCases.forEach { cases ->
            cases.country.apply {
                saveJson(target.resolve("total"), slug) {
                    stringify(cases)
                }
            }

        }
    }

}

private fun saveJson(directory: Path, fileName: String, parse: Json.() -> String) {
    Files.createDirectories(directory)
    prettyJson.parse().saveTo(output = directory.resolve("$fileName.pretty.json"))
    defaultJson.parse().saveTo(output = directory.resolve("$fileName.json"))
}

private fun List<Row>.asCasesByDayByCountry(): List<CasesByDayByCountry> =
    groupBy({ it.country }, { it.casesByDay })
        .mapNotNull { (country, days) ->
            CasesByDayByCountry(countryName = country.countryName, days = days)
        }

private val Row.casesByDay: CaseByDay
    get() = CaseByDay(
        day = localDate("Last_Update") ?: Date(encoded = Int.MIN_VALUE),
        cases = cases,
        delta = delta
    )

private val Row.cases
    get() = Cases(
        confirmed = int("Confirmed") ?: 0,
        deaths = int("Deaths") ?: 0,
        recovered = int("Recovered") ?: 0,
        active = int("Active") ?: 0
    )

private val Row.delta
    get() = Delta(
        deltaConfirmed = int("Delta_Confirmed") ?: 0,
        deltaRecovered = float("Delta_Recovered") ?: 0f
    )

private val Row.countryCases
    get() = CountryCases(
        countryName = country.countryName,
        cases = cases
    )

private val Row.country: Country
    get() = Country(string("Country_Region"))

internal fun String.saveTo(output: Path) {
    toByteArray().let { Files.write(output, it) }
}


private const val convidRepo = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19"

private const val csvPath: String = "$convidRepo/web-data/data/"

enum class Csv(private val fileName: String) {
    CasesByTime("cases_time"),
    Cases("cases"),
    CasesByState("cases_state"),
    CasesByCountry("cases_country");

    fun read(cache: Path): List<Row> =
        URL("$csvPath/$fileName.csv").saveTo(cache, "$fileName.csv").readCsv()

}
