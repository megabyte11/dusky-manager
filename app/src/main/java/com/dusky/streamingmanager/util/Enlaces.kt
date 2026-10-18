package com.dusky.streamingmanager.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

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
