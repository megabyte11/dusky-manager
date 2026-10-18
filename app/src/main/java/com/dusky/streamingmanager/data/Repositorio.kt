package com.dusky.streamingmanager.data

import androidx.room.withTransaction
import com.dusky.streamingmanager.util.calcularRenovacion
import com.dusky.streamingmanager.util.hoy
import kotlinx.coroutines.flow.Flow

/**
 * Único punto por donde la interfaz habla con la base de datos.
 * Así, si algún día cambia cómo se guardan los datos, solo se toca este archivo.
 */
class Repositorio(private val bd: AppDatabase) {

    private val suscripciones = bd.suscripcionDao()
    private val movimientosDao = bd.movimientoDao()
    private val proveedoresDao = bd.proveedorDao()
    private val serviciosDao = bd.servicioDao()

    val suscripcionesActivas: Flow<List<Suscripcion>> = suscripciones.observarActivas()
    val movimientos: Flow<List<Movimiento>> = movimientosDao.observarTodos()
    val proveedores: Flow<List<Proveedor>> = proveedoresDao.observarTodos()
    val servicios: Flow<List<Servicio>> = serviciosDao.observarTodos()
    val clientesConocidos: Flow<List<ClienteConocido>> = suscripciones.observarClientes()

    /** Crea el proveedor "Default" la primera vez que se abre la app. */
    suspend fun asegurarProveedorDefault() {
        if (proveedoresDao.contar() == 0) {
            proveedoresDao.insertar(Proveedor(nombre = "Default", color = 0xFF6B7280.toInt()))
        }
    }

    // ---------- Suscripciones ----------

    /** Guarda una suscripción nueva (id = 0) o actualiza una existente. */
    suspend fun guardarSuscripcion(s: Suscripcion) {
        if (s.id == 0L) suscripciones.insertar(s) else suscripciones.actualizar(s)
    }

    /**
     * Renueva: el periodo que termina queda registrado en el Historial y la suscripción
     * sigue activa con las fechas nuevas.
     */
    suspend fun renovar(s: Suscripcion, dias: Int, precio: Double) {
        bd.withTransaction {
            val periodo = calcularRenovacion(s.fechaVencimiento, hoy(), dias)
            movimientosDao.insertar(movimientoDe(s, TipoMovimiento.RENOVACION))
            suscripciones.actualizar(
                s.copy(fechaInicio = periodo.inicio, fechaVencimiento = periodo.vencimiento, precio = precio),
            )
        }
    }

    /** Cancela: queda en el Historial y se borra de la lista (con sus datos de acceso). */
    suspend fun cancelar(s: Suscripcion) {
        bd.withTransaction {
            movimientosDao.insertar(movimientoDe(s, TipoMovimiento.CANCELACION))
            suscripciones.eliminarTodas(listOf(s))
        }
    }

    /** Elimina sin dejar rastro en el Historial (para errores o datos de prueba). */
    suspend fun eliminar(lista: List<Suscripcion>) = suscripciones.eliminarTodas(lista)

    /** Deshace una eliminación (vuelve a insertar con los mismos datos). */
    suspend fun restaurar(lista: List<Suscripcion>) = suscripciones.insertarTodas(lista)

    private fun movimientoDe(s: Suscripcion, tipo: String) = Movimiento(
        tipo = tipo,
        nombreCliente = s.nombreCliente,
        servicioNombre = s.servicioNombre,
        costo = s.costo,
        precio = s.precio,
        fechaInicio = s.fechaInicio,
        fechaFin = s.fechaVencimiento,
        registradoEn = hoy(),
    )

    // ---------- Proveedores y servicios ----------

    /** Guarda el proveedor y devuelve su id. */
    suspend fun guardarProveedor(p: Proveedor): Long =
        if (p.id == 0L) {
            proveedoresDao.insertar(p)
        } else {
            proveedoresDao.actualizar(p)
            p.id
        }

    suspend fun eliminarProveedor(p: Proveedor) = proveedoresDao.eliminar(p)

    suspend fun contarSuscripcionesDeServicio(servicioId: Long): Int =
        suscripciones.contarPorServicio(servicioId)

    /**
     * Guarda el servicio. Si [actualizarSuscripciones] es true, el costo y el precio nuevos
     * también se aplican a las suscripciones activas de ese servicio.
     * El nombre y el color siempre se sincronizan para que las tarjetas no queden desactualizadas.
     */
    suspend fun guardarServicio(sv: Servicio, actualizarSuscripciones: Boolean) {
        bd.withTransaction {
            val id = if (sv.id == 0L) serviciosDao.insertar(sv) else {
                serviciosDao.actualizar(sv)
                sv.id
            }
            suscripciones.actualizarNombreColorPorServicio(id, sv.nombre, sv.color)
            if (actualizarSuscripciones) {
                suscripciones.actualizarCostoPorServicio(id, sv.costo)
                suscripciones.actualizarPrecioPorServicio(id, sv.precio)
            }
        }
    }

    suspend fun eliminarServicio(sv: Servicio) = serviciosDao.eliminar(sv)
}
