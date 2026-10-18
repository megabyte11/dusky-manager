package com.dusky.streamingmanager.util

import com.dusky.streamingmanager.data.Config
import com.dusky.streamingmanager.data.Suscripcion

/*
 * Mensajes de WhatsApp. Las plantillas usan palabras entre llaves que se reemplazan:
 *   {nombre} {servicio} {cuando} {fecha} {correo} {contrasena} {perfil} {pin}
 * Si una línea usa una palabra que está vacía (ej: {pin} sin PIN), esa línea se omite.
 */

const val PLANTILLA_RECORDATORIO =
    "Hola {nombre} 👋 Tu suscripción de {servicio} {cuando} ({fecha}). ¿Quieres renovarla? 😊"

const val PLANTILLA_VARIOS =
    "Hola {nombre} 👋 Tus suscripciones de {servicio} {cuando} ({fecha}). ¿Quieres renovarlas? 😊"

const val PLANTILLA_DATOS =
    "Hola {nombre} 👋 Estos son los datos de tu cuenta de {servicio}:\n" +
        "📧 Correo: {correo}\n" +
        "🔑 Contraseña: {contrasena}\n" +
        "👤 Perfil: {perfil}\n" +
        "🔢 PIN: {pin}\n" +
        "📅 Vence: {fecha}"

private val PALABRA = Regex("\\{(\\w+)\\}")

/** Reemplaza las {palabras} de la plantilla. Omite las líneas cuya palabra esté vacía. */
fun aplicarPlantilla(plantilla: String, valores: Map<String, String>): String =
    plantilla.lines().mapNotNull { linea ->
        var hayVacia = false
        val resultado = PALABRA.replace(linea) { coincidencia ->
            val clave = coincidencia.groupValues[1]
            if (!valores.containsKey(clave)) {
                coincidencia.value                 // palabra desconocida: se deja tal cual
            } else {
                val valor = valores.getValue(clave)
                if (valor.isBlank()) hayVacia = true
                valor
            }
        }
        if (hayVacia) null else resultado
    }.joinToString("\n")

/** "Netflix" / "Netflix y Max" / "Netflix, Max y Crunchyroll". */
fun unirNombres(nombres: List<String>): String = when (nombres.size) {
    0 -> ""
    1 -> nombres[0]
    else -> nombres.dropLast(1).joinToString(", ") + " y " + nombres.last()
}

/** "vence en 3 días", "vencen hoy", "venció hace 2 días"... */
fun textoCuando(dias: Long, plural: Boolean): String {
    val vence = if (plural) "vencen" else "vence"
    return when {
        dias < 0 -> {
            val verbo = if (plural) "vencieron" else "venció"
            "$verbo hace ${-dias} " + (if (-dias == 1L) "día" else "días")
        }
        dias == 0L -> "$vence hoy"
        dias == 1L -> "$vence mañana"
        else -> "$vence en $dias días"
    }
}

/**
 * Mensaje de recordatorio. [grupo] son las suscripciones activas del mismo cliente con la
 * misma fecha de vencimiento (incluye a [s]); si hay más de una se usa la plantilla "varios".
 */
fun mensajeRecordatorio(s: Suscripcion, grupo: List<Suscripcion>, config: Config): String {
    val varios = grupo.size > 1
    val servicios = unirNombres(grupo.map { it.servicioNombre }.distinct())
    val valores = mapOf(
        "nombre" to s.nombreCliente,
        "servicio" to servicios,
        "cuando" to textoCuando(diasRestantes(s.fechaVencimiento), varios),
        "fecha" to formatearFecha(s.fechaVencimiento),
    )
    return aplicarPlantilla(if (varios) config.plantillaVarios else config.plantillaRecordatorio, valores)
}

/** Mensaje con los datos de acceso de la cuenta. */
fun mensajeDatos(s: Suscripcion, config: Config): String {
    val valores = mapOf(
        "nombre" to s.nombreCliente,
        "servicio" to s.servicioNombre,
        "correo" to s.correo,
        "contrasena" to s.contrasena,
        "perfil" to (s.perfil ?: ""),
        "pin" to (s.pin ?: ""),
        "fecha" to formatearFecha(s.fechaVencimiento),
    )
    return aplicarPlantilla(config.plantillaDatos, valores)
}
