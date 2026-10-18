package com.dusky.streamingmanager.notificaciones

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dusky.streamingmanager.MainActivity
import com.dusky.streamingmanager.R
import com.dusky.streamingmanager.data.Config
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.util.diasRestantes
import com.dusky.streamingmanager.util.textoDias

/** Crea y muestra la notificación de vencimientos (una sola, con un resumen). */
object Notificador {

    const val CANAL = "vencimientos"
    private const val ID_NOTIFICACION = 1001

    /** Los canales son obligatorios desde Android 8. Crearlo varias veces no hace daño. */
    fun crearCanal(contexto: Context) {
        val canal = NotificationChannel(CANAL, "Vencimientos", NotificationManager.IMPORTANCE_DEFAULT)
        canal.description = "Avisos de suscripciones próximas a vencer"
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    /**
     * Avisa de las suscripciones que vencen en [Config.notifDiasAntes] días o menos
     * (y de las que vencieron hace 3 días o menos y siguen sin renovarse).
     * Si [siempre] es true, muestra la notificación aunque no haya nada (para probar).
     */
    @SuppressLint("MissingPermission") // se comprueba areNotificationsEnabled() justo abajo
    fun mostrar(contexto: Context, activas: List<Suscripcion>, config: Config, siempre: Boolean = false) {
        val manager = NotificationManagerCompat.from(contexto)
        if (!manager.areNotificationsEnabled()) return

        val limite = config.notifDiasAntes.toLong()
        val urgentes = activas.filter { diasRestantes(it.fechaVencimiento) in -3L..limite }
        if (urgentes.isEmpty() && !siempre) return

        crearCanal(contexto)

        val titulo = when (urgentes.size) {
            0 -> "Sin vencimientos próximos"
            1 -> "1 suscripción por vencer"
            else -> "${urgentes.size} suscripciones por vencer"
        }
        val lineas = urgentes.take(6).map {
            "${it.nombreCliente} · ${it.servicioNombre} — ${textoDias(diasRestantes(it.fechaVencimiento))}"
        }
        val estilo = NotificationCompat.InboxStyle()
        lineas.forEach { estilo.addLine(it) }
        if (urgentes.size > 6) estilo.addLine("+${urgentes.size - 6} más")

        val abrirApp = PendingIntent.getActivity(
            contexto,
            0,
            Intent(contexto, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notificacion = NotificationCompat.Builder(contexto, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(lineas.firstOrNull() ?: "Todo al día")
            .setStyle(estilo)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        manager.notify(ID_NOTIFICACION, notificacion)
    }
}
