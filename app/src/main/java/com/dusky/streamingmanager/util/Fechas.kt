package com.dusky.streamingmanager.util

import java.time.LocalDate
import java.time.format.DateTimeParseException

/*
 * Utilidades de fechas. Una fecha se guarda como "día del calendario" (epochDay):
 * un número entero que cuenta los días desde 1970-01-01. Así restar fechas es restar números.
 */

/** Hoy, como número de día. */
fun hoy(): Long = LocalDate.now().toEpochDay()

/** Días que faltan para el vencimiento (negativo = ya venció). */
fun diasRestantes(fechaVencimiento: Long): Long = fechaVencimiento - hoy()

/** Fecha legible: 2026-11-16 */
fun formatearFecha(dia: Long): String = LocalDate.ofEpochDay(dia).toString()

/** Convierte "2026-11-16" en número de día. Devuelve null si el texto no es una fecha válida. */
fun parsearFecha(texto: String): Long? =
    try {
        LocalDate.parse(texto.trim()).toEpochDay()
    } catch (e: DateTimeParseException) {
        null
    }

/** Qué tan urgente es un vencimiento. Define el color de la barra derecha. */
enum class NivelUrgencia { VENCIDO, ROJO, AMARILLO, VERDE }

fun nivelUrgencia(dias: Long): NivelUrgencia = when {
    dias < 0 -> NivelUrgencia.VENCIDO
    dias <= 5 -> NivelUrgencia.ROJO       // 0 a 5 días
    dias <= 10 -> NivelUrgencia.AMARILLO  // 6 a 10 días
    else -> NivelUrgencia.VERDE           // 11 días o más
}

/** Texto corto para la tarjeta: "Vence hoy", "3 días", "Venció hace 2 d". */
fun textoDias(dias: Long): String = when {
    dias < 0 -> "Venció hace ${-dias} d"
    dias == 0L -> "Vence hoy"
    dias == 1L -> "1 día"
    else -> "$dias días"
}
