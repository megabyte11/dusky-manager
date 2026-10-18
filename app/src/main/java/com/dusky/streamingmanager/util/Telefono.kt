package com.dusky.streamingmanager.util

/** Deja solo los dígitos: "+57 300-123 4567" -> "573001234567". */
fun soloDigitos(texto: String): String = texto.filter { it.isDigit() }

/**
 * Prepara un número para WhatsApp (wa.me exige código de país y solo dígitos).
 * Si el número tiene 10 dígitos o menos, se le antepone el código de país (ej: 57 para Colombia).
 */
fun normalizarTelefono(texto: String, codigoPais: String): String {
    val limpio = soloDigitos(texto).trimStart('0')
    val pais = soloDigitos(codigoPais)
    return if (limpio.length <= 10 && pais.isNotEmpty()) pais + limpio else limpio
}
