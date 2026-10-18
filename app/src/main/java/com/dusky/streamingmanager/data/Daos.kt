package com.dusky.streamingmanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/*
 * DAO = las consultas a la base de datos.
 * Las funciones que devuelven Flow avisan solas a la pantalla cuando los datos cambian.
 */

/** Cliente ya conocido (sirve para autocompletar el nombre al escribir un teléfono). */
data class ClienteConocido(val nombreCliente: String, val telefono: String)

@Dao
interface SuscripcionDao {
    @Query("SELECT * FROM suscripciones WHERE activa = 1 ORDER BY fechaVencimiento ASC")
    fun observarActivas(): Flow<List<Suscripcion>>

    @Query("SELECT * FROM suscripciones WHERE activa = 1 ORDER BY fechaVencimiento ASC")
    suspend fun activas(): List<Suscripcion>

    @Query("SELECT * FROM suscripciones")
    suspend fun todas(): List<Suscripcion>

    @Query("SELECT DISTINCT nombreCliente, telefono FROM suscripciones ORDER BY nombreCliente ASC")
    fun observarClientes(): Flow<List<ClienteConocido>>

    @Query("SELECT COUNT(*) FROM suscripciones WHERE servicioId = :servicioId AND activa = 1")
    suspend fun contarPorServicio(servicioId: Long): Int

    @Query("UPDATE suscripciones SET costo = :costo WHERE servicioId = :servicioId AND activa = 1")
    suspend fun actualizarCostoPorServicio(servicioId: Long, costo: Double)

    @Query("UPDATE suscripciones SET precio = :precio WHERE servicioId = :servicioId AND activa = 1")
    suspend fun actualizarPrecioPorServicio(servicioId: Long, precio: Double)

    @Query("UPDATE suscripciones SET servicioNombre = :nombre, servicioColor = :color WHERE servicioId = :servicioId")
    suspend fun actualizarNombreColorPorServicio(servicioId: Long, nombre: String, color: Int)

    @Query("DELETE FROM suscripciones")
    suspend fun borrarTodo()

    @Insert
    suspend fun insertar(suscripcion: Suscripcion): Long

    @Insert
    suspend fun insertarTodas(lista: List<Suscripcion>)

    @Update
    suspend fun actualizar(suscripcion: Suscripcion)

    @Delete
    suspend fun eliminarTodas(lista: List<Suscripcion>)
}

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos")
    suspend fun todos(): List<Movimiento>

    @Query("DELETE FROM movimientos")
    suspend fun borrarTodo()

    @Insert
    suspend fun insertar(movimiento: Movimiento): Long

    @Insert
    suspend fun insertarTodos(lista: List<Movimiento>)
}

@Dao
interface ProveedorDao {
    @Query("SELECT * FROM proveedores ORDER BY id ASC")
    fun observarTodos(): Flow<List<Proveedor>>

    @Query("SELECT * FROM proveedores")
    suspend fun todos(): List<Proveedor>

    @Query("SELECT COUNT(*) FROM proveedores")
    suspend fun contar(): Int

    @Query("DELETE FROM proveedores")
    suspend fun borrarTodo()

    @Insert
    suspend fun insertar(proveedor: Proveedor): Long

    @Insert
    suspend fun insertarTodos(lista: List<Proveedor>)

    @Update
    suspend fun actualizar(proveedor: Proveedor)

    @Delete
    suspend fun eliminar(proveedor: Proveedor)
}

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios")
    suspend fun todos(): List<Servicio>

    @Query("DELETE FROM servicios")
    suspend fun borrarTodo()

    @Insert
    suspend fun insertar(servicio: Servicio): Long

    @Insert
    suspend fun insertarTodos(lista: List<Servicio>)

    @Update
    suspend fun actualizar(servicio: Servicio)

    @Delete
    suspend fun eliminar(servicio: Servicio)
}
