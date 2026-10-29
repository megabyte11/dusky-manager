package com.dusky.streamingmanager.data

/** Cliente ya registrado (sirve para sugerirlo y autocompletar al agregar otro servicio). */
data class ClienteConocido(
    val nombreCliente: String,
    val telefono: String,
    val usuarioWhatsApp: String? = null,
)
