package com.dusky.streamingmanager.ui.ajustes

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.BotonPrincipal
import com.dusky.streamingmanager.ui.components.CampoTexto
import com.dusky.streamingmanager.ui.components.PantallaConTitulo
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.PLANTILLA_DATOS
import com.dusky.streamingmanager.util.PLANTILLA_RECORDATORIO
import com.dusky.streamingmanager.util.PLANTILLA_VARIOS

/** Edita los textos de los mensajes de WhatsApp y el código de país. */
@Composable
fun MensajesScreen(vm: MainViewModel, alVolver: () -> Unit) {
    val config by vm.config.collectAsStateWithLifecycle()

    var pais by remember { mutableStateOf(config.codigoPais) }
    var recordatorio by remember { mutableStateOf(config.plantillaRecordatorio) }
    var varios by remember { mutableStateOf(config.plantillaVarios) }
    var datos by remember { mutableStateOf(config.plantillaDatos) }

    PantallaConTitulo("Mensajes de WhatsApp", alVolver) {
        Tarjeta {
            Subtitulo("Palabras que puedes usar")
            Text(
                "{nombre} {servicio} {cuando} {fecha} {correo} {contrasena} {perfil} {pin}\n\n" +
                    "Se reemplazan solas al enviar. Si una línea usa una palabra que está vacía " +
                    "(por ejemplo {pin} cuando no hay PIN), esa línea se omite.",
                color = Paleta.TextoSecundario,
            )
        }

        Tarjeta {
            CampoTexto(
                valor = pais,
                alCambiar = { pais = it },
                etiqueta = "Código de país (ej: 57)",
                teclado = KeyboardType.Number,
                ayuda = "Se antepone a los números de 10 dígitos o menos.",
            )
            CampoTexto(recordatorio, { recordatorio = it }, "Recordatorio (un servicio)", lineas = 4)
            CampoTexto(
                valor = varios,
                alCambiar = { varios = it },
                etiqueta = "Recordatorio (varios servicios, misma fecha)",
                lineas = 4,
            )
            CampoTexto(datos, { datos = it }, "Datos de la cuenta", lineas = 8)
        }

        BotonPrincipal("Guardar", habilitado = pais.isNotBlank()) {
            vm.guardarConfig(
                config.copy(
                    codigoPais = pais.trim(),
                    plantillaRecordatorio = recordatorio,
                    plantillaVarios = varios,
                    plantillaDatos = datos,
                ),
            )
            alVolver()
        }

        OutlinedButton(
            onClick = {
                recordatorio = PLANTILLA_RECORDATORIO
                varios = PLANTILLA_VARIOS
                datos = PLANTILLA_DATOS
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Restaurar mensajes originales") }
    }
}
