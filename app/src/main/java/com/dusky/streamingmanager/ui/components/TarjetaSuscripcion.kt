package com.dusky.streamingmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.NivelUrgencia
import com.dusky.streamingmanager.util.diasRestantes
import com.dusky.streamingmanager.util.dinero
import com.dusky.streamingmanager.util.nivelUrgencia
import com.dusky.streamingmanager.util.textoDias

/** Traduce el nivel de urgencia a un color. */
private fun colorDe(nivel: NivelUrgencia): Color = when (nivel) {
    NivelUrgencia.VENCIDO -> Paleta.Gris
    NivelUrgencia.ROJO -> Paleta.Rojo
    NivelUrgencia.AMARILLO -> Paleta.Amarillo
    NivelUrgencia.VERDE -> Paleta.Verde
}

/**
 * Una fila de la lista principal:
 *  [barra del servicio] nombre + servicio ........ precio + días [barra de urgencia]
 * (En la Fase 2 se podrá tocar para expandirla.)
 */
@Composable
fun TarjetaSuscripcion(suscripcion: Suscripcion, modifier: Modifier = Modifier) {
    val dias = diasRestantes(suscripcion.fechaVencimiento)
    val colorUrgencia = colorDe(nivelUrgencia(dias))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(Paleta.Superficie),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Barra izquierda: color del servicio
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color(suscripcion.servicioColor)),
        )

        Column(Modifier.weight(1f).padding(12.dp)) {
            Text(
                text = suscripcion.nombreCliente,
                color = Paleta.Texto,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = suscripcion.servicioNombre,
                color = Paleta.TextoSecundario,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(text = dinero(suscripcion.precio), color = Paleta.Texto)
            Text(text = textoDias(dias), color = colorUrgencia, fontWeight = FontWeight.Bold)
        }

        // Barra derecha: urgencia según los días que faltan
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(colorUrgencia),
        )
    }
}
