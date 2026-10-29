package com.dusky.streamingmanager.util

import com.dusky.streamingmanager.data.ClienteConocido

/**
 * Clientes ya registrados que coinciden con lo que se está escribiendo.
 *  - Sin escribir nada: los más recientes.
 *  - Con nombre, teléfono y/o usuario parcial: solo los que coinciden.
 *  - Si lo escrito ya es exactamente un cliente registrado, no se sugiere nada.
 * [conocidos] debe venir ordenada del más reciente al más antiguo.
 */
fun sugerirClientes(
    conocidos: List<ClienteConocido>,
    nombre: String,
    telefono: String,
    usuario: String = "",
    maximo: Int = 8,
): List<ClienteConocido> {
    val consulta = nombre.trim().lowercase()
    val digitos = soloDigitos(telefono)
    val usuarioEscrito = limpiarUsuario(usuario)
    return conocidos
        .distinctBy { claveCliente(it.telefono, it.usuarioWhatsApp) }
        .filter { c ->
            val tel = soloDigitos(c.telefono)
            val user = c.usuarioWhatsApp.orEmpty()
            val coincideNombre = consulta.isEmpty() || c.nombreCliente.lowercase().contains(consulta)
            val coincideTelefono = digitos.isEmpty() || tel.contains(digitos)
            val coincideUsuario = usuarioEscrito.isEmpty() || user.contains(usuarioEscrito)
            val mismoNombre = consulta.isNotEmpty() && c.nombreCliente.trim().lowercase() == consulta
            val yaEscrito = mismoNombre &&
                ((digitos.isNotEmpty() && tel == digitos) || (usuarioEscrito.isNotEmpty() && user == usuarioEscrito))
            coincideNombre && coincideTelefono && coincideUsuario && !yaEscrito
        }
        .take(maximo)
}

/** Lo que se muestra junto al nombre en la cápsula: últimos 4 dígitos del teléfono o el @usuario. */
fun etiquetaContacto(c: ClienteConocido): String {
    val digitos = soloDigitos(c.telefono)
    return if (digitos.isNotEmpty()) digitos.takeLast(4) else "@" + c.usuarioWhatsApp.orEmpty()
}
