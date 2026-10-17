package com.dusky.streamingmanager.util

import com.dusky.streamingmanager.data.Movimiento
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.data.TipoMovimiento

/** Los números de las 6 tarjetas de arriba. */
data class Resumen(
    val ventas: Double = 0.0,      // lo que pagaron los clientes activos
    val costos: Double = 0.0,      // lo que te costaron esas cuentas
    val ganancia: Double = 0.0,    // ventas - costos
    val asegurada: Double = 0.0,   // ganancia de periodos que ya se completaron (historial)
    val activos: Int = 0,          // cantidad de suscripciones activas
    val porVencer: Int = 0,        // vencen en 5 días o menos (incluye las ya vencidas sin renovar)
)

fun calcularResumen(activas: List<Suscripcion>, movimientos: List<Movimiento>): Resumen {
    val ventas = activas.sumOf { it.precio }
    val costos = activas.sumOf { it.costo }
    val asegurada = movimientos
        .filter { it.tipo == TipoMovimiento.RENOVACION || it.tipo == TipoMovimiento.VENCIMIENTO }
        .sumOf { it.precio - it.costo }
    return Resumen(
        ventas = ventas,
        costos = costos,
        ganancia = ventas - costos,
        asegurada = asegurada,
        activos = activas.size,
        porVencer = activas.count { diasRestantes(it.fechaVencimiento) <= 5 },
    )
}
