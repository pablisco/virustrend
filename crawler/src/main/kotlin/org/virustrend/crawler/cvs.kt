package org.virustrend.crawler

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal typealias Row = Map<String, String>

internal fun URL.readCsv(): List<Row> = csvReader().readAllWithHeader(get())

internal fun Row.cell(key: String): String =
    checkNotNull(this[key]) { "key \"$key\" not found" }

internal fun <T : Any> Row.cell(key: String, transform: (String) -> T?): T =
    checkNotNull(transform(cell(key))) {
        "Failed to parse value \"${cell(key)}\" for \"$key\""
    }

internal fun Row.int(key: String): Int = cell(key) { it.toIntOrNull() }
internal fun Row.float(key: String): Float = cell(key) { it.toFloatOrNull() }

private fun URL.get(): InputStream =
    with(openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        inputStream
    }
