package com.dusky.streamingmanager.ui

/** Las pantallas a las que se puede ir. [Principal] tiene las 3 pestañas (Clientes, Historial, Ajustes). */
sealed interface Pantalla {
    data object Principal : Pantalla
    // suscripcionId: null = cliente nuevo | copiarDeId: copia los datos del cliente de esa suscripción ("+ Otro servicio")
    data class Formulario(val suscripcionId: Long?, val copiarDeId: Long? = null) : Pantalla
    data class EditorProveedor(val proveedorId: Long?) : Pantalla     // null = proveedor nuevo
    data class EditorServicio(val proveedorId: Long, val servicioId: Long?) : Pantalla
    data object Mensajes : Pantalla
    data object Notificaciones : Pantalla
}
