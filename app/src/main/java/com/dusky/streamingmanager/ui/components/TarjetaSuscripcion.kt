package com.dusky.streamingmanager.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dusky.streamingmanager.data.Proveedor
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.NivelUrgencia
import com.dusky.streamingmanager.util.diasRestantes
import com.dusky.streamingmanager.util.dinero
import com.dusky.streamingmanager.util.formatearFecha
import com.dusky.streamingmanager.util.nivelUrgencia
import com.dusky.streamingmanager.util.textoDias

/** Traduce el nivel de urgencia a un color. */
private fun colorDe(nivel: NivelUrgencia): Color = when (nivel) {
    NivelUrgencia.VENCIDO -> Paleta.Gris
    NivelUrgencia.ROJO -> Paleta.Rojo
    NivelUrgencia.AMARILLO -> Paleta.Amarillo
    NivelUrgencia.VERDE -> Paleta.Verde
}

/** Lo que hacen los botones de una tarjeta expandida. Los chats de proveedor son null si no hay número. */
class AccionesTarjeta(
    val recordar: () -> Unit,
    val enviarDatos: () -> Unit,
    val renovar: () -> Unit,
    val editar: () -> Unit,
    val chatVentas: (() -> Unit)?,
    val chatSoporte: (() -> Unit)?,
)

/**
 * Una suscripción en la lista:
 *  [barra del servicio] icono · cliente + servicio ........ precio + días [barra de urgencia]
 * Al tocarla se expande (si [acciones] no es null) y muestra los datos y botones.
 * Al mantenerla pulsada se selecciona (para eliminar).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaSuscripcion(
    s: Suscripcion,
    icono: String?,
    proveedor: Proveedor?,
    seleccionada: Boolean,
    alTocar: () -> Unit,
    alMantener: () -> Unit,
    acciones: AccionesTarjeta?,
    modifier: Modifier = Modifier,
) {
    val dias = diasRestantes(s.fechaVencimiento)
    val colorUrgencia = colorDe(nivelUrgencia(dias))
    val forma = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(forma)
            .background(Paleta.Superficie)
            .then(if (seleccionada) Modifier.border(2.dp, Paleta.Azul, forma) else Modifier)
            .combinedClickable(onClick = alTocar, onLongClick = alMantener),
    ) {
        // Barra izquierda: color del servicio
        Box(
            Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(Color(s.servicioColor)),
        )

        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icono(icono, s.servicioColor, s.servicioNombre)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = s.nombreCliente,
                        color = Paleta.Texto,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = s.servicioNombre,
                        color = Paleta.TextoSecundario,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = dinero(s.precio), color = Paleta.Texto)
                    Text(text = textoDias(dias), color = colorUrgencia, fontWeight = FontWeight.Bold)
                }
            }
            if (acciones != null) {
                Detalle(s, dias, proveedor, acciones)
            }
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

/** Parte que aparece al expandir: datos de la cuenta y botones. */
@Composable
private fun Detalle(s: Suscripcion, dias: Long, proveedor: Proveedor?, acciones: AccionesTarjeta) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Paleta.Borde))
        Spacer(Modifier.height(6.dp))

        // Quién vende esta cuenta, con acceso directo a sus chats
        if (proveedor != null) {
            FilaProveedor(proveedor, acciones.chatVentas, acciones.chatSoporte)
            Spacer(Modifier.height(4.dp))
        }

        Dato("Teléfono", s.telefono)
        Dato("Tu costo", dinero(s.costo), Paleta.Rojo)
        Dato("Venta", dinero(s.precio), Paleta.Verde)
        Dato("Ganancia", dinero(s.precio - s.costo), Paleta.Azul)
        Dato("Registro", formatearFecha(s.fechaInicio))
        Dato("Vencimiento", formatearFecha(s.fechaVencimiento))
        Dato("Tiempo restante", textoDias(dias), colorDe(nivelUrgencia(dias)))
        if (s.correo.isNotBlank()) Dato("Correo", s.correo)
        if (s.contrasena.isNotBlank()) Dato("Contraseña", s.contrasena)
        if (!s.perfil.isNullOrBlank()) Dato("Perfil", s.perfil)
        if (!s.pin.isNullOrBlank()) Dato("PIN", s.pin)
        if (!s.nota.isNullOrBlank()) Dato("Nota", s.nota)

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonAccion("💬 Recordar", Paleta.WhatsApp, Modifier.weight(1f), acciones.recordar)
            BotonAccion("🔑 Enviar datos", Paleta.WhatsApp, Modifier.weight(1f), acciones.enviarDatos)
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonAccion("Renovar", Paleta.Azul, Modifier.weight(1f), acciones.renovar)
            BotonAccion("Editar", Paleta.SuperficieAlta, Modifier.weight(1f), acciones.editar, Paleta.Texto)
        }
    }
}

/** Icono y nombre del proveedor, con botones pequeños a sus chats de ventas y soporte (si tiene número). */
@Composable
private fun FilaProveedor(proveedor: Proveedor, chatVentas: (() -> Unit)?, chatSoporte: (() -> Unit)?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icono(proveedor.icono, proveedor.color, proveedor.nombre, tamano = 32.dp)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Proveedor", color = Paleta.TextoSecundario, fontSize = 12.sp)
            Text(
                text = proveedor.nombre,
                color = Paleta.Texto,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (chatVentas != null) BotonChat("Ventas", chatVentas)
        if (chatSoporte != null) {
            Spacer(Modifier.width(6.dp))
            BotonChat("Soporte", chatSoporte)
        }
    }
}

@Composable
private fun BotonChat(texto: String, alTocar: () -> Unit) {
    OutlinedButton(
        onClick = alTocar,
        modifier = Modifier.height(34.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
    ) {
        Text(texto, color = Paleta.Texto, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String, colorValor: Color = Paleta.Texto) {
    Row(Modifier.fillMaxWidth()) {
        Text(etiqueta, color = Paleta.TextoSecundario, modifier = Modifier.width(112.dp))
        Text(valor, color = colorValor, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BotonAccion(
    texto: String,
    fondo: Color,
    modifier: Modifier,
    alTocar: () -> Unit,
    colorTexto: Color = Color.Black,
) {
    Button(
        onClick = alTocar,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = fondo, contentColor = colorTexto),
    ) {
        Text(texto, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
