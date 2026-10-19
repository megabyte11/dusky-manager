package com.dusky.streamingmanager.util

import com.dusky.streamingmanager.data.ClienteConocido

/**
 * Clientes ya registrados que coinciden con lo que se está escribiendo.
 *  - Sin escribir nada: los más recientes.
 *  - Con nombre y/o teléfono parcial: solo los que coinciden.
 *  - Si lo escrito ya es exactamente un cliente registrado, no se sugiere nada.
 * [conocidos] debe venir ordenada del más reciente al más antiguo.
 */
fun sugerirClientes(
    conocidos: List<ClienteConocido>,
    nombre: String,
    telefono: String,
    maximo: Int = 8,
): List<ClienteConocido> {
    val consulta = nombre.trim().lowercase()
    val digitos = soloDigitos(telefono)
    return conocidos
        .distinctBy { soloDigitos(it.telefono) }
        .filter { c ->
            val tel = soloDigitos(c.telefono)
            val coincideNombre = consulta.isEmpty() || c.nombreCliente.lowercase().contains(consulta)
            val coincideTelefono = digitos.isEmpty() || tel.contains(digitos)
            val yaEscrito = consulta.isNotEmpty() && digitos.isNotEmpty() &&
                c.nombreCliente.trim().lowercase() == consulta && tel == digitos
            coincideNombre && coincideTelefono && !yaEscrito
        }
        .take(maximo)
}
