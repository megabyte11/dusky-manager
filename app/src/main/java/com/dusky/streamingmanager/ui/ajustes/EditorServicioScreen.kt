package com.dusky.streamingmanager.ui.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.Servicio
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.BotonPeligro
import com.dusky.streamingmanager.ui.components.BotonPrincipal
import com.dusky.streamingmanager.ui.components.CampoTexto
import com.dusky.streamingmanager.ui.components.PantallaConTitulo
import com.dusky.streamingmanager.ui.components.SelectorColor
import com.dusky.streamingmanager.ui.components.SelectorImagen
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.COLORES_SERVICIO
import com.dusky.streamingmanager.util.colorSugerido
import com.dusky.streamingmanager.util.numeroATexto
import com.dusky.streamingmanager.util.textoANumero
import kotlinx.coroutines.launch

/**
 * Crear o editar un servicio de un proveedor (nombre, cuenta/perfil, costo, precio, días, color, icono).
 * Si cambias el costo o el precio de un servicio que ya tiene clientes, pregunta si también
 * se actualizan sus suscripciones activas.
 */
@Composable
fun EditorServicioScreen(
    vm: MainViewModel,
    proveedorId: Long,
    servicioId: Long?,
    alVolver: () -> Unit,
) {
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val existente = servicioId?.let { id -> servicios.find { it.id == id } }

    var nombre by remember { mutableStateOf(existente?.nombre ?: "") }
    var esPerfil by remember { mutableStateOf(existente?.esPerfil ?: false) }
    var costo by remember { mutableStateOf(existente?.let { numeroATexto(it.costo) } ?: "") }
    var precio by remember { mutableStateOf(existente?.let { numeroATexto(it.precio) } ?: "") }
    var dias by remember { mutableStateOf((existente?.diasPorDefecto ?: 30).toString()) }
    var color by remember { mutableIntStateOf(existente?.color ?: COLORES_SERVICIO[6]) }
    var icono by remember { mutableStateOf(existente?.icono) }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    // Pregunta pendiente: servicio a guardar y cuántas suscripciones activas se verían afectadas
    var pendiente by remember { mutableStateOf<Servicio?>(null) }
    var afectadas by remember { mutableIntStateOf(0) }

    PantallaConTitulo(
        titulo = if (existente == null) "Nuevo servicio" else "Servicio",
        alVolver = alVolver,
    ) {
        Tarjeta {
            CampoTexto(
                valor = nombre,
                alCambiar = { nuevo ->
                    // En servicios nuevos, el color se sugiere según el nombre (Netflix rojo, etc.)
                    if (existente == null) color = colorSugerido(nuevo)
                    nombre = nuevo
                },
                etiqueta = "Nombre *",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(if (esPerfil) "Es un perfil" else "Es una cuenta completa", color = Paleta.Texto)
                Switch(checked = esPerfil, onCheckedChange = { esPerfil = it })
            }
            CampoTexto(costo, { costo = it }, "Costo (lo que pagas tú) *", teclado = KeyboardType.Decimal)
            CampoTexto(precio, { precio = it }, "Precio de venta *", teclado = KeyboardType.Decimal)
            CampoTexto(dias, { dias = it }, "Duración por defecto (días)", teclado = KeyboardType.Number)
            Subtitulo("Color")
            SelectorColor(color) { color = it }
            Subtitulo("Icono (opcional)")
            SelectorImagen(icono, color, nombre) { icono = it }
        }

        if (error != null) {
            Text(error ?: "", color = Paleta.Rojo)
        }

        BotonPrincipal("Guardar") {
            val c = textoANumero(costo)
            val p = textoANumero(precio)
            val d = dias.toIntOrNull()
            val problema = when {
                nombre.isBlank() -> "Escribe el nombre del servicio."
                c == null -> "El costo no es un número válido."
                p == null -> "El precio no es un número válido."
                d == null || d <= 0 -> "Los días deben ser un número mayor a 0."
                else -> null
            }
            if (problema != null) {
                error = problema
            } else {
                val nuevo = (existente ?: Servicio(proveedorId = proveedorId, nombre = "", costo = 0.0, precio = 0.0, color = 0))
                    .copy(
                        nombre = nombre.trim(),
                        esPerfil = esPerfil,
                        costo = c!!,
                        precio = p!!,
                        color = color,
                        icono = icono,
                        diasPorDefecto = d!!,
                    )
                scope.launch {
                    val cambiaronPrecios = existente != null && (existente.costo != nuevo.costo || existente.precio != nuevo.precio)
                    val n = if (cambiaronPrecios && existente != null) {
                        vm.contarSuscripcionesDeServicio(existente.id)
                    } else {
                        0
                    }
                    if (n > 0) {
                        afectadas = n
                        pendiente = nuevo
                    } else {
                        vm.guardarServicio(nuevo, false)
                        alVolver()
                    }
                }
            }
        }

        if (existente != null) {
            BotonPeligro("Eliminar servicio") { confirmarEliminar = true }
        }
    }

    pendiente?.let { nuevo ->
        AlertDialog(
            onDismissRequest = { pendiente = null },
            title = { Text("¿Actualizar los clientes actuales?") },
            text = {
                Text(
                    "Cambiaste el costo o el precio. ¿Quieres aplicarlo también a las $afectadas " +
                        "suscripciones activas de este servicio?",
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.guardarServicio(nuevo, true)
                    alVolver()
                }) { Text("Sí, actualizar todas") }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.guardarServicio(nuevo, false)
                    alVolver()
                }) { Text("Solo para nuevos") }
            },
        )
    }

    if (confirmarEliminar && existente != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar ${existente.nombre}?") },
            text = { Text("Las suscripciones que ya tienen este servicio no se borran.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarServicio(existente)
                    alVolver()
                }) { Text("Sí, eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } },
        )
    }
}
