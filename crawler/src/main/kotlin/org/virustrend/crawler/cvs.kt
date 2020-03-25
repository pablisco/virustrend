package org.virustrend.crawler

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.soywiz.klock.Date
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Path

import org.virustrend.toLocalDate

internal typealias Row = Map<String, String>

internal fun File.readCsv(): List<Row> = csvReader().readAllWithHeader(this)

internal fun URL.saveTo(directory: Path, fileName: String) =
    directory.resolve(fileName).toFile().apply {
        parentFile.mkdirs()
        outputStream().use { get().copyTo(it) }
    }
internal fun Row.string(key: String): String =
    checkNotNull(this[key]) { "key \"$key\" not found" }

internal fun <T : Any> Row.cell(key: String, transform: (String) -> T?): T? =
    transform(string(key))

internal fun Row.int(key: String): Int? = cell(key) { it.toIntOrNull() }
internal fun Row.float(key: String): Float? = cell(key) { it.toFloatOrNull() }
internal fun Row.localDate(key: String): Date? = cell(key) { it.toLocalDate() }

private fun URL.get(): InputStream =
    with(openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        inputStream
    }
