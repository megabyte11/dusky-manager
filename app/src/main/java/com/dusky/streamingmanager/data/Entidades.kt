package com.dusky.streamingmanager.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
 * ENTIDADES = las tablas de la base de datos.
 *
 * Convenciones:
 *  - Las fechas se guardan como "día del calendario" (Long). Ver util/Fechas.kt.
 *  - Los colores se guardan como Int (formato ARGB de Android).
 *  - No hay tabla "Cliente": el cliente es simplemente nombre + teléfono dentro de cada suscripción.
 */

/** Quien te vende las cuentas. Siempre existe uno llamado "Default" (sin foto ni número). */
@Entity(tableName = "proveedores")
data class Proveedor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val color: Int,
    val icono: String? = null,            // ruta de la imagen guardada en la app (opcional)
    val whatsappVentas: String? = null,   // número del chat de ventas (opcional)
    val whatsappSoporte: String? = null,  // número del chat de soporte (opcional)
)

/** Un servicio que vende un proveedor (ej: Netflix perfil). Guarda precios por defecto. */
@Entity(
    tableName = "servicios",
    foreignKeys = [
        ForeignKey(
            entity = Proveedor::class,
            parentColumns = ["id"],
            childColumns = ["proveedorId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("proveedorId")],
)
data class Servicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val proveedorId: Long,
    val nombre: String,
    val esPerfil: Boolean = false,   // false = cuenta completa, true = perfil
    val costo: Double,               // lo que te cuesta a ti
    val precio: Double,              // lo que cobras al cliente
    val color: Int,
    val icono: String? = null,
    val diasPorDefecto: Int = 30,
)

/**
 * Una suscripción = una tarjeta en la lista principal.
 * Copia el nombre y color del servicio para que el historial no cambie
 * aunque después edites o borres el servicio.
 */
@Entity(tableName = "suscripciones", indices = [Index("telefono")])
data class Suscripcion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreCliente: String,
    val telefono: String,
    val servicioId: Long? = null,
    val proveedorId: Long? = null,
    val servicioNombre: String,
    val servicioColor: Int,
    val costo: Double,
    val precio: Double,
    val fechaInicio: Long,
    val fechaVencimiento: Long,
    val correo: String = "",
    val contrasena: String = "",
    val perfil: String? = null,
    val pin: String? = null,
    val nota: String? = null,
    val activa: Boolean = true,      // false = cancelada
)

/** Una línea del Historial: una renovación, un vencimiento o una cancelación. */
@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: String,                // ver TipoMovimiento
    val nombreCliente: String,
    val servicioNombre: String,
    val costo: Double,
    val precio: Double,
    val fechaInicio: Long,
    val fechaFin: Long,
    val registradoEn: Long,
)

/** Valores posibles de [Movimiento.tipo]. */
object TipoMovimiento {
    const val RENOVACION = "RENOVACION"
    const val VENCIMIENTO = "VENCIMIENTO"
    const val CANCELACION = "CANCELACION"
}
