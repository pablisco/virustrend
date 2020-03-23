package org.virustrend.crawler

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate

internal typealias Row = Map<String, String>

internal fun URL.readCsv(): List<Row> = csvReader().readAllWithHeader(get())

internal fun Row.string(key: String): String =
    checkNotNull(this[key]) { "key \"$key\" not found" }

internal fun <T : Any> Row.cell(key: String, transform: (String) -> T?): T? =
    transform(string(key))

internal fun Row.int(key: String): Int? = this.cell(key) { it.toIntOrNull() }
internal fun Row.float(key: String): Float? = this.cell(key) { it.toFloatOrNull() }
internal fun Row.localDate(key: String): LocalDate? = this.cell(key) { it.toLocalDate() }

private fun URL.get(): InputStream =
    with(openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        inputStream
    }
