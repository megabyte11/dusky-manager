package com.dusky.streamingmanager.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.formatearFecha

/** Pestaña Historial: renovaciones, vencimientos y cancelaciones (versión inicial, se mejora en la Fase 5). */
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
                    text = "Aquí aparecerán las renovaciones, vencimientos y cancelaciones.",
                    color = Paleta.TextoSecundario,
                )
            }
        }
        items(movimientos, key = { it.id }) { m ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Paleta.Superficie)
                    .padding(12.dp),
            ) {
                Text(m.tipo, color = Paleta.Azul, fontWeight = FontWeight.Bold)
                Text("${m.nombreCliente} · ${m.servicioNombre}", color = Paleta.Texto)
                Text(
                    "${formatearFecha(m.fechaInicio)} a ${formatearFecha(m.fechaFin)}",
                    color = Paleta.TextoSecundario,
                )
            }
        }
    }
}
