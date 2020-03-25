package org.virustrend

import com.soywiz.klock.Date

fun String.toLocalDate(): Date? =
    split("/").takeIf { it.size >= 3 }
        ?.map { it.toInt() }
        ?.let { (month, day, year) ->
            Date(year = 2000 + year, month = month, day = day)
        }
