package com.dusky.streamingmanager.notificaciones

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Programa (o cancela) la revisión diaria de vencimientos. */
object Programador {

    private const val NOMBRE = "revision_diaria"

    /**
     * Programa la revisión una vez al día a la [hora] indicada (0 a 23).
     * Con [reemplazar] = true se ajusta a la hora nueva; con false se respeta la ya programada.
     * Android puede retrasar un poco la ejecución para ahorrar batería.
     */
    fun programar(contexto: Context, hora: Int, reemplazar: Boolean) {
        val ahora = LocalDateTime.now()
        var objetivo = ahora.toLocalDate().atTime(hora, 0)
        if (!objetivo.isAfter(ahora)) objetivo = objetivo.plusDays(1)
        val espera = Duration.between(ahora, objetivo).toMillis()

        val peticion = PeriodicWorkRequestBuilder<RevisarVencimientosWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(espera, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(contexto).enqueueUniquePeriodicWork(
            NOMBRE,
            if (reemplazar) ExistingPeriodicWorkPolicy.UPDATE else ExistingPeriodicWorkPolicy.KEEP,
            peticion,
        )
    }

    fun cancelar(contexto: Context) {
        WorkManager.getInstance(contexto).cancelUniqueWork(NOMBRE)
    }
}
