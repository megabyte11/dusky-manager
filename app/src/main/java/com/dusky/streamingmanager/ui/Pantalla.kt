package com.dusky.streamingmanager.ui

/** Las pantallas a las que se puede ir. [Principal] tiene las 3 pestañas (Clientes, Historial, Ajustes). */
sealed interface Pantalla {
    data object Principal : Pantalla
    data class Formulario(val suscripcionId: Long?) : Pantalla        // null = cliente nuevo
    data class EditorProveedor(val proveedorId: Long?) : Pantalla     // null = proveedor nuevo
    data class EditorServicio(val proveedorId: Long, val servicioId: Long?) : Pantalla
    data object Mensajes : Pantalla
    data object Notificaciones : Pantalla
}
