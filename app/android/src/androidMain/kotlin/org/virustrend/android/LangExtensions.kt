package org.virustrend.android

fun <T> List<T>?.orEmpty(): List<T> = this ?: emptyList()