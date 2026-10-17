package com.dusky.streamingmanager.data

import com.dusky.streamingmanager.util.hoy
import kotlinx.coroutines.flow.Flow

/**
 * Único punto por donde la interfaz habla con la base de datos.
 * Así, si algún día cambia cómo se guardan los datos, solo se toca este archivo.
 */
class Repositorio(private val bd: AppDatabase) {

    val suscripcionesActivas: Flow<List<Suscripcion>> = bd.suscripcionDao().observarActivas()
    val movimientos: Flow<List<Movimiento>> = bd.movimientoDao().observarTodos()
    val proveedores: Flow<List<Proveedor>> = bd.proveedorDao().observarTodos()

    /** Crea el proveedor "Default" la primera vez que se abre la app. */
    suspend fun asegurarProveedorDefault() {
        val dao = bd.proveedorDao()
        if (dao.contar() == 0) {
            dao.insertar(Proveedor(nombre = "Default", color = 0xFF6B7280.toInt()))
        }
    }

    /** SOLO PARA PROBAR (Fase 1): agrega 3 suscripciones de ejemplo. Se elimina en la Fase 2. */
    suspend fun agregarSuscripcionesDePrueba() {
        val h = hoy()
        val ejemplos = listOf(
            Triple("Netflix", 0xFFE50914.toInt(), 3L),
            Triple("Crunchyroll", 0xFFF47521.toInt(), 8L),
            Triple("Max", 0xFF5B3CC4.toInt(), 25L),
        )
        ejemplos.forEach { (servicio, color, dias) ->
            bd.suscripcionDao().insertar(
                Suscripcion(
                    nombreCliente = "Cliente de prueba",
                    telefono = "3000000000",
                    servicioNombre = servicio,
                    servicioColor = color,
                    costo = 8.0,
                    precio = 15.0,
                    fechaInicio = h,
                    fechaVencimiento = h + dias,
                ),
            )
        }
    }
}
