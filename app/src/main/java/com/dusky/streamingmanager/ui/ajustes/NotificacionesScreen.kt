package com.dusky.streamingmanager.ui.ajustes

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.PantallaConTitulo
import com.dusky.streamingmanager.ui.components.Paso
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta

/** Activa o desactiva el aviso diario, elige la hora y cuántos días antes avisar. */
@Composable
fun NotificacionesScreen(vm: MainViewModel, alVolver: () -> Unit) {
    val contexto = LocalContext.current
    val config by vm.config.collectAsStateWithLifecycle()
    var permitidas by remember { mutableStateOf(vm.notificacionesPermitidas()) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permitidas = vm.notificacionesPermitidas()
    }

    PantallaConTitulo("Notificaciones", alVolver) {
        Tarjeta {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Avisar de vencimientos", color = Paleta.Texto)
                Switch(
                    checked = config.notifActivas,
                    onCheckedChange = { vm.guardarConfig(config.copy(notifActivas = it)) },
                )
            }
            Paso(
                etiqueta = "Hora del aviso",
                valor = "%02d:00".format(config.notifHora),
                alBajar = { vm.guardarConfig(config.copy(notifHora = (config.notifHora + 23) % 24)) },
                alSubir = { vm.guardarConfig(config.copy(notifHora = (config.notifHora + 1) % 24)) },
            )
            Paso(
                etiqueta = "Avisar cuando falten",
                valor = "${config.notifDiasAntes} días o menos",
                alBajar = { vm.guardarConfig(config.copy(notifDiasAntes = maxOf(0, config.notifDiasAntes - 1))) },
                alSubir = { vm.guardarConfig(config.copy(notifDiasAntes = minOf(30, config.notifDiasAntes + 1))) },
            )
            Text(
                "Llega una sola notificación al día con el resumen. Si una suscripción venció hace " +
                    "3 días o menos y no se ha renovado, también aparece.",
                color = Paleta.TextoSecundario,
            )
        }

        Tarjeta {
            Subtitulo("Permiso y prueba")
            Text(
                if (permitidas) "Las notificaciones están permitidas." else "Las notificaciones están bloqueadas para esta app.",
                color = if (permitidas) Paleta.Verde else Paleta.Rojo,
            )
            if (!permitidas && Build.VERSION.SDK_INT >= 33) {
                OutlinedButton(
                    onClick = { pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Permitir notificaciones") }
            }
            OutlinedButton(
                onClick = { vm.probarNotificacion() },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Enviar notificación de prueba") }
        }

        Tarjeta {
            Subtitulo("Si no te llegan los avisos")
            Text(
                "Xiaomi / HyperOS cierra las apps en segundo plano para ahorrar batería. En los ajustes de la app " +
                    "pon el ahorro de batería en \"Sin restricciones\" y activa el \"Inicio automático\".",
                color = Paleta.TextoSecundario,
            )
            OutlinedButton(
                onClick = {
                    contexto.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", contexto.packageName, null)
                        },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Abrir ajustes de la app") }
        }
    }
}
