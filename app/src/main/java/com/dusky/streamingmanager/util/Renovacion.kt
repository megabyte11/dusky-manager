package com.dusky.streamingmanager.util

/** Nuevo periodo de una suscripción renovada. */
data class PeriodoRenovado(val inicio: Long, val vencimiento: Long)

/**
 * Si la suscripción todavía está vigente, la renovación se suma al vencimiento actual
 * (el cliente no pierde días). Si ya venció, cuenta desde hoy.
 */
fun calcularRenovacion(vencimientoActual: Long, hoy: Long, dias: Int): PeriodoRenovado {
    val inicio = maxOf(vencimientoActual, hoy)
    return PeriodoRenovado(inicio = inicio, vencimiento = inicio + dias)
}
