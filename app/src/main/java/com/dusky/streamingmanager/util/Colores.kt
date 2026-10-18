package com.dusky.streamingmanager.util

/** Paleta fija para proveedores y servicios (colores ARGB). */
val COLORES_SERVICIO: List<Int> = listOf(
    0xFFE50914.toInt(), // rojo
    0xFFF47521.toInt(), // naranja
    0xFFE3B341.toInt(), // amarillo
    0xFF3FB950.toInt(), // verde
    0xFF14B8A6.toInt(), // turquesa
    0xFF38BDF8.toInt(), // celeste
    0xFF58A6FF.toInt(), // azul
    0xFF5B3CC4.toInt(), // índigo
    0xFFBC8CFF.toInt(), // morado
    0xFFF472B6.toInt(), // rosa
    0xFF6B7280.toInt(), // gris
    0xFFE6EDF3.toInt(), // blanco
)

/** Color sugerido según el nombre del servicio (los conocidos tienen su color de marca). */
fun colorSugerido(nombre: String): Int {
    val n = nombre.lowercase()
    return when {
        "netflix" in n -> 0xFFE50914.toInt()
        "crunchy" in n -> 0xFFF47521.toInt()
        "disney" in n -> 0xFF113CCF.toInt()
        "hbo" in n || n.startsWith("max") -> 0xFF5B3CC4.toInt()
        "prime" in n || "amazon" in n -> 0xFF00A8E1.toInt()
        "spotify" in n -> 0xFF1DB954.toInt()
        "youtube" in n -> 0xFFFF0000.toInt()
        "paramount" in n -> 0xFF0064FF.toInt()
        else -> COLORES_SERVICIO[(n.hashCode() and 0x7fffffff) % COLORES_SERVICIO.size]
    }
}
