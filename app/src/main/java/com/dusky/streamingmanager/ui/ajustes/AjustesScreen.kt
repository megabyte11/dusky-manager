package com.dusky.streamingmanager.ui.ajustes

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.BuildConfig
import com.dusky.streamingmanager.R
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.Pantalla
import com.dusky.streamingmanager.ui.components.BotonPrincipal
import com.dusky.streamingmanager.ui.components.Icono
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.abrirEnlace
import com.dusky.streamingmanager.util.formatearFecha
import com.dusky.streamingmanager.util.hoy
import kotlinx.coroutines.launch

/**
 * Enlace que se abre al tocar el dibujo de Dusky.
 * Cámbialo por el tuyo (por ejemplo "https://x.com/tu_usuario").
 */
private const val ENLACE_DUSKY = "https://github.com/megabyte11"

/**
 * Pestaña Ajustes: Dusky, proveedores, mensajes de WhatsApp, notificaciones y respaldo.
 */
@Composable
fun AjustesScreen(vm: MainViewModel, irA: (Pantalla) -> Unit) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val proveedores by vm.proveedores.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()

    var importarUri by remember { mutableStateOf<Uri?>(null) }

    val lanzadorExportar = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bien = vm.exportar(uri)
                Toast.makeText(
                    contexto,
                    if (bien) "Respaldo guardado" else "No se pudo guardar el respaldo",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
    val lanzadorImportar = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> importarUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Dusky termina justo donde empieza la tarjeta: parece que se asoma por detrás.
        // Al tocarlo se abre el enlace de ENLACE_DUSKY.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.dusky),
                contentDescription = "Dusky",
                modifier = Modifier
                    .height(130.dp)
                    .clickable { abrirEnlace(contexto, ENLACE_DUSKY) },
                contentScale = ContentScale.Fit,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Paleta.Superficie)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Dusky",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Paleta.Texto,
                )
                Text("Gestor de suscripciones de streaming", color = Paleta.TextoSecundario)
                Text("Versión ${BuildConfig.VERSION_NAME} · código abierto, hecho con ayuda de IA (Claude)", color = Paleta.TextoSecundario)
            }
        }

        Tarjeta {
            Subtitulo("Proveedores")
            proveedores.forEach { p ->
                val cantidad = servicios.count { it.proveedorId == p.id }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { irA(Pantalla.EditorProveedor(p.id)) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icono(p.icono, p.color, p.nombre)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.nombre, color = Paleta.Texto, fontWeight = FontWeight.Bold)
                        Text(
                            if (cantidad == 1) "1 servicio" else "$cantidad servicios",
                            color = Paleta.TextoSecundario,
                        )
                    }
                    Text("›", color = Paleta.TextoSecundario)
                }
            }
            BotonPrincipal("+ Nuevo proveedor") { irA(Pantalla.EditorProveedor(null)) }
        }

        Tarjeta {
            Subtitulo("Preferencias")
            FilaEnlace("Mensajes de WhatsApp") { irA(Pantalla.Mensajes) }
            FilaEnlace("Notificaciones de vencimiento") { irA(Pantalla.Notificaciones) }
        }

        Tarjeta {
            Subtitulo("Respaldo")
            Text(
                "Guarda todos tus datos en un archivo para no perderlos si cambias de celular. " +
                    "Incluye correos y contraseñas: guárdalo en un lugar privado y no lo subas a GitHub.",
                color = Paleta.TextoSecundario,
            )
            OutlinedButton(
                onClick = { lanzadorExportar.launch("respaldo-dusky-${formatearFecha(hoy())}.json") },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Exportar respaldo") }
            OutlinedButton(
                onClick = { lanzadorImportar.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Importar respaldo") }
        }
    }

    importarUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { importarUri = null },
            title = { Text("¿Importar respaldo?") },
            text = { Text("Esto reemplazará TODOS los datos actuales por los del archivo.") },
            confirmButton = {
                TextButton(onClick = {
                    importarUri = null
                    scope.launch {
                        val bien = vm.importar(uri)
                        Toast.makeText(
                            contexto,
                            if (bien) "Respaldo importado" else "El archivo no es un respaldo válido",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }) { Text("Sí, reemplazar") }
            },
            dismissButton = { TextButton(onClick = { importarUri = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun FilaEnlace(texto: String, alTocar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alTocar)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(texto, color = Paleta.Texto, modifier = Modifier.weight(1f))
        Text("›", color = Paleta.TextoSecundario)
    }
}
