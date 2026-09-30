package com.example.petcard.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Ayudas de fecha/hora compatibles con minSdk 24 (sin `java.time`).
 *
 * Convención del proyecto: las fechas se guardan como `Long` = milisegundos
 * del inicio del día en la zona horaria local; las horas como `Int` = minutos
 * desde medianoche (ej. 600 = 10:00).
 */
object Dates {
    const val MILLIS_POR_DIA = 24L * 60L * 60L * 1000L

    fun inicioDiaMillis(anio: Int, mes1a12: Int, dia: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, anio)
        cal.set(Calendar.MONTH, mes1a12 - 1)
        cal.set(Calendar.DAY_OF_MONTH, dia)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun hoyInicioMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Convierte un inicio-de-día local a los millis UTC que el Material3
     * `DatePicker` necesita para resaltar el día correcto en cualquier zona.
     */
    fun paraDatePicker(inicioDiaLocal: Long): Long =
        inicioDiaLocal + java.util.TimeZone.getDefault().getOffset(inicioDiaLocal)

    fun formatearFecha(millis: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))

    fun formatearFechaCorta(millis: Long): String =
        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))

    fun formatearHora(horaMinutos: Int): String {
        val h = horaMinutos / 60
        val m = horaMinutos % 60
        return String.format(Locale.getDefault(), "%02d:%02d", h, m)
    }

    fun nombreMes(anio: Int, mes1a12: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, anio)
        cal.set(Calendar.MONTH, mes1a12 - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val nombre = SimpleDateFormat("MMMM", Locale("es")).format(cal.time)
        return nombre.replaceFirstChar { it.uppercase() }
    }

    fun desglosar(millis: Long): Triple<Int, Int, Int> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return Triple(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
        )
    }
}
