package com.dusky.streamingmanager.util

/*
 * Contacto de un cliente: teléfono y/o usuario de WhatsApp (@usuario).
 * Desde 2026 WhatsApp permite usuarios opcionales; un cliente que te escribe desde un usuario
 * puede no mostrarte su número.
 */

/**
 * Limpia un usuario escrito o pegado: quita "@", espacios y símbolos, y lo pasa a minúsculas.
 * También entiende enlaces pegados: "https://wa.me/ana.perez" -> "ana.perez".
 * WhatsApp solo permite letras minúsculas, números, puntos y guion bajo.
 */
fun limpiarUsuario(texto: String): String {
    var t = texto.trim().lowercase()
    t = t.substringAfter("wa.me/", t)       // enlace pegado: nos quedamos con lo que va después
    t = t.substringBefore("?")              // fuera los parámetros del enlace
    return t.filter { it in 'a'..'z' || it in '0'..'9' || it == '.' || it == '_' }
}

/** Un usuario es aceptable si tiene entre 3 y 35 caracteres (ya viene limpio con [limpiarUsuario]). */
fun usuarioValido(usuario: String): Boolean = usuario.length in 3..35

/**
 * Identidad de un cliente para agrupar sus suscripciones (ej: recordatorio combinado):
 * el teléfono si lo tiene; si no, su usuario de WhatsApp.
 */
fun claveCliente(telefono: String, usuario: String?): String {
    val digitos = soloDigitos(telefono).trimStart('0')
    return when {
        digitos.isNotEmpty() -> digitos
        !usuario.isNullOrBlank() -> "@" + usuario.lowercase()
        else -> ""
    }
}
