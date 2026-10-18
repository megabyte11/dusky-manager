package com.dusky.streamingmanager.ui.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.components.CampoTexto
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.calcularRenovacion
import com.dusky.streamingmanager.util.formatearFecha
import com.dusky.streamingmanager.util.hoy
import com.dusky.streamingmanager.util.numeroATexto
import com.dusky.streamingmanager.util.textoANumero

/** Ventana para renovar: días y precio (ya rellenos) y muestra el nuevo vencimiento. */
@Composable
fun DialogoRenovar(
    s: Suscripcion,
    diasPorDefecto: Int,
    alCancelar: () -> Unit,
    alConfirmar: (dias: Int, precio: Double) -> Unit,
) {
    var dias by remember { mutableStateOf(diasPorDefecto.toString()) }
    var precio by remember { mutableStateOf(numeroATexto(s.precio)) }
    val d = dias.toIntOrNull()
    val p = textoANumero(precio)
    val periodo = d?.let { calcularRenovacion(s.fechaVencimiento, hoy(), it) }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Renovar ${s.servicioNombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoTexto(dias, { dias = it }, "Días", teclado = KeyboardType.Number)
                CampoTexto(precio, { precio = it }, "Precio de venta", teclado = KeyboardType.Decimal)
                if (periodo != null) {
                    Text("Nuevo vencimiento: ${formatearFecha(periodo.vencimiento)}", color = Paleta.TextoSecundario)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = d != null && d > 0 && p != null,
                onClick = { alConfirmar(d!!, p!!) },
            ) { Text("Renovar") }
        },
        dismissButton = { TextButton(onClick = alCancelar) { Text("Cancelar") } },
    )
}
