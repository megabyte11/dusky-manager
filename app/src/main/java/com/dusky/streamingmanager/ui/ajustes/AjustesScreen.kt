package com.dusky.streamingmanager.ui.ajustes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dusky.streamingmanager.R
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.abrirEnlace

/**
 * Enlace que se abre al tocar el dibujo de Dusky.
 * Cámbialo por el tuyo (por ejemplo "https://x.com/tu_usuario").
 */
private const val ENLACE_DUSKY = "https://github.com/megabyte11"

/**
 * Pestaña Ajustes. Por ahora solo muestra a Dusky asomándose sobre una tarjeta.
 * En la Fase 3 aquí van proveedores, servicios, plantillas de WhatsApp y notificaciones.
 */
@Composable
fun AjustesScreen() {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // El dibujo termina justo donde empieza la tarjeta, así parece que se asoma por detrás.
        // Al tocarlo se abre el enlace de ENLACE_DUSKY.
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
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Dusky",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Paleta.Texto,
            )
            Text("Gestor de suscripciones de streaming", color = Paleta.TextoSecundario)
            Text("Versión 0.1.0 · código abierto, hecho con ayuda de IA (Claude)", color = Paleta.TextoSecundario)
            Text(
                text = "Pronto aquí: proveedores, servicios, mensajes de WhatsApp y notificaciones.",
                color = Paleta.Texto,
            )
        }
    }
}
