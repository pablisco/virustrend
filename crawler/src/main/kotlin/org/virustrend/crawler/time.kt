package org.virustrend.crawler

import kotlinx.serialization.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun String.toLocalDate(): LocalDate? =
    split("/").takeIf { it.size >= 3 }
        ?.map { it.toInt() }
        ?.let { (month, day, year) ->
            LocalDate.of(2000 + year, month, day)
        }

object LocalDateSerializer : KSerializer<LocalDate> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE

    override val descriptor: SerialDescriptor = PrimitiveDescriptor(
        serialName = "java.time.LocalDate",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(value.format(formatter))

    override fun deserialize(decoder: Decoder): LocalDate =
        decoder.decodeString().let { it.toLocalDate() ?: error("Can't decode $it to LocalDate") }

}