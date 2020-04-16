package org.virustrend.domain

enum class DataMetric {
    Confirmed, Deaths, Recovered, Active;

    companion object {
        val default = Confirmed
    }

}