package com.dusky.streamingmanager.data

import com.dusky.streamingmanager.util.PLANTILLA_DATOS
import com.dusky.streamingmanager.util.PLANTILLA_RECORDATORIO
import com.dusky.streamingmanager.util.PLANTILLA_VARIOS

/** Ajustes de la app (se guardan en ConfigStore). */
data class Config(
    val codigoPais: String = "57",
    val plantillaRecordatorio: String = PLANTILLA_RECORDATORIO,
    val plantillaVarios: String = PLANTILLA_VARIOS,
    val plantillaDatos: String = PLANTILLA_DATOS,
    val notifActivas: Boolean = true,
    val notifHora: Int = 9,          // hora del aviso diario (0 a 23)
    val notifDiasAntes: Int = 3,     // avisar cuando falten estos días o menos
    val permisoPreguntado: Boolean = false,
)
