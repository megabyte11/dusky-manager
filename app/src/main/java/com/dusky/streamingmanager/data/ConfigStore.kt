package com.dusky.streamingmanager.data

import android.content.Context

/** Guarda y lee los ajustes en un archivo privado de la app (SharedPreferences). */
class ConfigStore(contexto: Context) {

    private val prefs = contexto.applicationContext
        .getSharedPreferences("dusky_config", Context.MODE_PRIVATE)

    fun leer(): Config {
        val d = Config()
        return Config(
            codigoPais = prefs.getString("codigoPais", d.codigoPais) ?: d.codigoPais,
            plantillaRecordatorio = prefs.getString("plantillaRecordatorio", d.plantillaRecordatorio) ?: d.plantillaRecordatorio,
            plantillaVarios = prefs.getString("plantillaVarios", d.plantillaVarios) ?: d.plantillaVarios,
            plantillaDatos = prefs.getString("plantillaDatos", d.plantillaDatos) ?: d.plantillaDatos,
            notifActivas = prefs.getBoolean("notifActivas", d.notifActivas),
            notifHora = prefs.getInt("notifHora", d.notifHora),
            notifDiasAntes = prefs.getInt("notifDiasAntes", d.notifDiasAntes),
            permisoPreguntado = prefs.getBoolean("permisoPreguntado", d.permisoPreguntado),
        )
    }

    fun guardar(c: Config) {
        prefs.edit()
            .putString("codigoPais", c.codigoPais)
            .putString("plantillaRecordatorio", c.plantillaRecordatorio)
            .putString("plantillaVarios", c.plantillaVarios)
            .putString("plantillaDatos", c.plantillaDatos)
            .putBoolean("notifActivas", c.notifActivas)
            .putInt("notifHora", c.notifHora)
            .putInt("notifDiasAntes", c.notifDiasAntes)
            .putBoolean("permisoPreguntado", c.permisoPreguntado)
            .apply()
    }
}
