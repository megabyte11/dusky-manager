package com.dusky.streamingmanager.notificaciones

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dusky.streamingmanager.data.AppDatabase
import com.dusky.streamingmanager.data.ConfigStore

/**
 * Tarea que Android ejecuta una vez al día (ver [Programador]).
 * No hay ningún servicio corriendo todo el tiempo: Android despierta la app, esta tarea
 * revisa los vencimientos, avisa si hace falta y se termina.
 */
class RevisarVencimientosWorker(
    contexto: Context,
    parametros: WorkerParameters,
) : CoroutineWorker(contexto, parametros) {

    override suspend fun doWork(): Result {
        val config = ConfigStore(applicationContext).leer()
        if (config.notifActivas) {
            val activas = AppDatabase.obtener(applicationContext).suscripcionDao().activas()
            Notificador.mostrar(applicationContext, activas, config)
        }
        return Result.success()
    }
}
