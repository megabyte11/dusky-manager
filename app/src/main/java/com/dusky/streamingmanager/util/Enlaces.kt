package com.dusky.streamingmanager.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Abre un enlace en la app que corresponda (navegador, GitHub, X, WhatsApp...).
 * Si el celular no tiene ninguna app capaz de abrirlo, no pasa nada (la app no se cierra).
 */
fun abrirEnlace(contexto: Context, url: String) {
    val intento = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        contexto.startActivity(intento)
    } catch (e: ActivityNotFoundException) {
        // No hay app para abrir este enlace: se ignora.
    }
}

/**
 * Abre un chat de WhatsApp con [telefono]. Si hay [mensaje], queda escrito y listo para enviar
 * (la persona solo tiene que tocar "enviar"). Usa enlaces wa.me: no necesita permisos.
 */
fun abrirWhatsApp(contexto: Context, telefono: String, codigoPais: String, mensaje: String? = null) {
    val numero = normalizarTelefono(telefono, codigoPais)
    val url = if (mensaje == null) {
        "https://wa.me/$numero"
    } else {
        "https://wa.me/$numero?text=" + Uri.encode(mensaje)
    }
    abrirEnlace(contexto, url)
}

/**
 * Envía un mensaje por WhatsApp a un cliente.
 *  - Con teléfono: abre directamente el chat con el mensaje escrito.
 *  - Solo con usuario (@usuario): WhatsApp todavía no tiene un enlace oficial para abrir un chat
 *    por usuario, así que se abre WhatsApp con el mensaje listo y tú eliges el chat del cliente.
 */
fun enviarPorWhatsApp(
    contexto: Context,
    telefono: String,
    usuario: String?,
    codigoPais: String,
    mensaje: String,
) {
    if (telefono.isNotBlank()) {
        abrirWhatsApp(contexto, telefono, codigoPais, mensaje)
    } else {
        Toast.makeText(contexto, "Elige el chat de @${usuario.orEmpty()} para enviar el mensaje", Toast.LENGTH_LONG).show()
        abrirEnlace(contexto, "https://wa.me/?text=" + Uri.encode(mensaje))
    }
}
