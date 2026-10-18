package com.dusky.streamingmanager.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Toma la imagen elegida en la galería, la recorta cuadrada, la reduce a 128x128 px
 * y la guarda dentro de la app. Devuelve la ruta del archivo (o null si algo falla).
 * Así los iconos pesan muy poco.
 */
fun guardarIcono(contexto: Context, uri: Uri): String? {
    return try {
        // 1) Medir la imagen sin cargarla, para leerla ya reducida
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contexto.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }
        var muestreo = 1
        while (medidas.outWidth / (muestreo * 2) >= 256 && medidas.outHeight / (muestreo * 2) >= 256) {
            muestreo *= 2
        }

        // 2) Cargarla reducida
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
        val original = contexto.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        } ?: return null

        // 3) Recortar cuadrada desde el centro y escalar a 128x128
        val lado = minOf(original.width, original.height)
        val recorte = Bitmap.createBitmap(
            original,
            (original.width - lado) / 2,
            (original.height - lado) / 2,
            lado,
            lado,
        )
        val reducida = Bitmap.createScaledBitmap(recorte, 128, 128, true)

        // 4) Guardar como PNG en la carpeta privada de la app
        val carpeta = File(contexto.filesDir, "iconos").apply { mkdirs() }
        val archivo = File(carpeta, UUID.randomUUID().toString() + ".png")
        FileOutputStream(archivo).use { reducida.compress(Bitmap.CompressFormat.PNG, 100, it) }
        archivo.absolutePath
    } catch (e: Exception) {
        null
    }
}
