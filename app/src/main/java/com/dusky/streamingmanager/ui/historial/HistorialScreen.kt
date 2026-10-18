package com.dusky.streamingmanager.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.TipoMovimiento
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.dinero
import com.dusky.streamingmanager.util.formatearFecha

/** Pestaña Historial: renovaciones (periodos completados) y cancelaciones, lo más reciente arriba. */
@Composable
fun HistorialScreen(vm: MainViewModel) {
    val movimientos by vm.movimientos.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (movimientos.isEmpty()) {
            item {
                Text(
                    text = "Aquí aparecerán las renovaciones y cancelaciones.",
                    color = Paleta.TextoSecundario,
                )
            }
        }
        items(movimientos, key = { it.id }) { m ->
            val esRenovacion = m.tipo == TipoMovimiento.RENOVACION
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Paleta.Superficie)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = if (esRenovacion) "Renovación" else "Cancelación",
                        color = if (esRenovacion) Paleta.Verde else Paleta.Rojo,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(dinero(m.precio - m.costo), color = Paleta.Azul, fontWeight = FontWeight.Bold)
                }
                Text("${m.nombreCliente} · ${m.servicioNombre}", color = Paleta.Texto)
                Text(
                    "Periodo: ${formatearFecha(m.fechaInicio)} a ${formatearFecha(m.fechaFin)}",
                    color = Paleta.TextoSecundario,
                )
                Text("Registrado: ${formatearFecha(m.registradoEn)}", color = Paleta.TextoSecundario)
            }
        }
    }
}
