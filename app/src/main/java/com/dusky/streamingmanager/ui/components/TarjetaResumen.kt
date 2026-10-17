package com.dusky.streamingmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.Resumen
import com.dusky.streamingmanager.util.dinero

/** Una tarjeta pequeña de arriba: título gris y valor de color. */
@Composable
fun TarjetaResumen(
    titulo: String,
    valor: String,
    colorValor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Paleta.Superficie)
            .padding(12.dp),
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            color = Paleta.TextoSecundario,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = colorValor,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

/** Las 6 tarjetas en dos filas de tres (ver util/Resumen.kt para saber qué significa cada una). */
@Composable
fun PanelResumen(resumen: Resumen, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Ventas", dinero(resumen.ventas), Paleta.Verde, Modifier.weight(1f))
            TarjetaResumen("Costos", dinero(resumen.costos), Paleta.Rojo, Modifier.weight(1f))
            TarjetaResumen("Ganancia", dinero(resumen.ganancia), Paleta.Azul, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaResumen("Asegurada", dinero(resumen.asegurada), Paleta.Morado, Modifier.weight(1f))
            TarjetaResumen("Activos", resumen.activos.toString(), Paleta.Texto, Modifier.weight(1f))
            TarjetaResumen("Vencen ≤5 días", resumen.porVencer.toString(), Paleta.Amarillo, Modifier.weight(1f))
        }
    }
}
