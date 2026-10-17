package com.dusky.streamingmanager.util

import java.util.Locale

/** Dinero con 2 decimales: $2,000.00. Si prefieres otro formato (pesos, puntos), se cambia aquí. */
fun dinero(valor: Double): String = String.format(Locale.US, "\$%,.2f", valor)
