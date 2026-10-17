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

@Dao
interface SuscripcionDao {
    /** Suscripciones activas, las que vencen primero arriba. */
    @Query("SELECT * FROM suscripciones WHERE activa = 1 ORDER BY fechaVencimiento ASC")
    fun observarActivas(): Flow<List<Suscripcion>>

    @Insert
    suspend fun insertar(suscripcion: Suscripcion): Long

    @Update
    suspend fun actualizar(suscripcion: Suscripcion)

    @Delete
    suspend fun eliminar(suscripcion: Suscripcion)
}

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Insert
    suspend fun insertar(movimiento: Movimiento): Long
}

@Dao
interface ProveedorDao {
    @Query("SELECT * FROM proveedores ORDER BY id ASC")
    fun observarTodos(): Flow<List<Proveedor>>

    @Query("SELECT COUNT(*) FROM proveedores")
    suspend fun contar(): Int

    @Insert
    suspend fun insertar(proveedor: Proveedor): Long

    @Update
    suspend fun actualizar(proveedor: Proveedor)

    @Delete
    suspend fun eliminar(proveedor: Proveedor)
}

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios WHERE proveedorId = :proveedorId ORDER BY nombre ASC")
    fun observarPorProveedor(proveedorId: Long): Flow<List<Servicio>>

    @Insert
    suspend fun insertar(servicio: Servicio): Long

    @Update
    suspend fun actualizar(servicio: Servicio)

    @Delete
    suspend fun eliminar(servicio: Servicio)
}
