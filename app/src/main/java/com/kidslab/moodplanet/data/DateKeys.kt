package com.kidslab.moodplanet.data

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Utilidades para convertir fechas a la clave "yyyy-MM-dd" usada en Room. */
object DateKeys {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): String = LocalDate.now(ZoneId.systemDefault()).format(formatter)

    fun daysAgo(days: Long): String =
        LocalDate.now(ZoneId.systemDefault()).minusDays(days).format(formatter)

    /** Últimos [days] días incluyendo hoy, en orden ascendente (más antiguo primero). */
    fun lastDays(days: Int): List<String> {
        val today = LocalDate.now(ZoneId.systemDefault())
        return (days - 1 downTo 0).map { offset -> today.minusDays(offset.toLong()).format(formatter) }
    }

    fun parse(dateKey: String): LocalDate = LocalDate.parse(dateKey, formatter)
}
