package com.dusky.streamingmanager.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Abre un enlace en la app que corresponda (navegador, GitHub, X, WhatsApp...).
 * Si el celular no tiene ninguna app capaz de abrirlo, no pasa nada (la app no se cierra).
 * Más adelante se reutiliza para los enlaces de WhatsApp.
 */
fun abrirEnlace(contexto: Context, url: String) {
    val intento = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        contexto.startActivity(intento)
    } catch (e: ActivityNotFoundException) {
        // No hay app para abrir este enlace: se ignora.
    }
}
