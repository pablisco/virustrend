package org.virustrend.crawler

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
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
    timeCasesUrl.download(output = target.resolve("timeCases.pretty.json")) { parseDayInfo(prettyJson) }
    timeCasesUrl.download(output = target.resolve("timeCases.json")) { parseDayInfo(defaultJson) }
}

private fun List<Row>.parseDayInfo(json: Json): String =
    map {
        it.cell("Country_Region") to DayInfo(
            day = it.cell("Last_Update") { it.toLocalDate() },
            confirmed = it.int("Confirmed"),
            deaths = it.int("Deaths"),
            recovered = it.int("Recovered"),
            active = it.int("Active"),
            deltaConfirmed = it.int("Delta_Confirmed"),
            deltaRecovered = it.float("Delta_Recovered")
        )
    }.groupBy({ it.first }, { it.second }).let { json.stringify(DayInfoMapSerializer, it) }

private fun URL.download(output: Path, parseRows: List<Row>.() -> String) {
    readCsv().parseRows().toByteArray().let { Files.write(output, it) }
}

private val DayInfoMapSerializer: KSerializer<Map<String, List<DayInfo>>> =
    MapSerializer(String.serializer(), ListSerializer(DayInfo.serializer()))

@Serializable
data class DayInfo(
    @Serializable(with = LocalDateSerializer::class) val day: LocalDate,
    val confirmed: Int,
    val deaths: Int,
    val recovered: Int,
    val active: Int,
    val deltaConfirmed: Int,
    val deltaRecovered: Float
)


private const val convidRepo = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19"
private val timeCasesUrl = URL("$convidRepo/web-data/data/cases_time.csv")

