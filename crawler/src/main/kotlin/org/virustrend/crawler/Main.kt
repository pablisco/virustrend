package org.virustrend.crawler

import com.soywiz.klock.Date
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.virustrend.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@ImplicitReflectionSerializer
fun main(args: Array<String>) {
    val workingDirectory = args.firstOrNull() ?: error("missing working directory")
    val target = Paths.get(workingDirectory).resolve("../build/pages/api")
    val cache = Paths.get(workingDirectory).resolve("../build/pages/cache")
    val (casesByTime, casesUrl, casesByStateUrl, casesByCountryUrl) =
        Csv.values().map { it.read(cache) }
    val casesByDayByCountry: List<CasesByDayByCountry> = casesByTime.asCasesByDayByCountry()

    saveJson(target, "daily") {
        stringify(casesByDayByCountry)
    }

    saveJson(target, "countries") {
        stringify(casesByDayByCountry.map { Country(it.countryName) })
    }

    casesByDayByCountry.forEach { (countryName, casesByDay) ->
        saveJson(target.resolve("daily"), countryName.urlEncode()) {
            stringify(casesByDay)
        }
    }

    saveJson(target, "global-info") {
        casesByDayByCountry
            .mapNotNull { (_, timeCases) -> timeCases.maxBy { it.day }?.cases }
            .reduce { current, next -> current + next }
            .let { compositeDataPoint -> GlobalInfo(dataPoint = compositeDataPoint) }
            .let { stringify(it) }
    }

}

private fun saveJson(directory: Path, fileName: String, parse: Json.() -> String) {
    Files.createDirectories(directory)
    prettyJson.parse().saveTo(output = directory.resolve("$fileName.pretty.json"))
    defaultJson.parse().saveTo(output = directory.resolve("$fileName.json"))
}

private fun List<Row>.asCasesByDayByCountry(): List<CasesByDayByCountry> =
    map { it.toTimeCase() }
        .groupBy({ it.first }, { it.second })
        .map { (countryName, days) ->
            CasesByDayByCountry(countryName = countryName, days = days)
        }

private fun Row.toTimeCase() =
    string("Country_Region") to CaseByDay(
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

internal fun String.saveTo(output: Path) {
    toByteArray().let { Files.write(output, it) }
}


private const val convidRepo = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19"

private const val csvPath: String = "$convidRepo/web-data/data/"

enum class Csv(private val fileName: String) {
    CasesByTime("cases_time"),
    CasesUrl("cases"),
    CasesByStateUrl("cases_state"),
    CasesByCountryUrl("cases_country");

    fun read(cache: Path)  : List<Row> =
        URL("$csvPath/$fileName.csv").saveTo(cache, "$fileName.csv").readCsv()

}