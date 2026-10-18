package com.dusky.streamingmanager.util

import java.util.Locale

/** Dinero con 2 decimales: $2,000.00. Si prefieres otro formato (pesos, puntos), se cambia aquí. */
fun dinero(valor: Double): String = String.format(Locale.US, "\$%,.2f", valor)

/** Número para mostrarlo en un campo de texto: 8.0 -> "8", 8.5 -> "8.5". */
fun numeroATexto(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toLong().toString() else valor.toString()

/** Lee un número escrito por el usuario (acepta coma o punto). Devuelve null si no es válido. */
fun textoANumero(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()
