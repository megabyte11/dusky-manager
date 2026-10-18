package com.dusky.streamingmanager.ui.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.Proveedor
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.Pantalla
import com.dusky.streamingmanager.ui.components.BotonPeligro
import com.dusky.streamingmanager.ui.components.BotonPrincipal
import com.dusky.streamingmanager.ui.components.CampoTexto
import com.dusky.streamingmanager.ui.components.Icono
import com.dusky.streamingmanager.ui.components.PantallaConTitulo
import com.dusky.streamingmanager.ui.components.SelectorColor
import com.dusky.streamingmanager.ui.components.SelectorImagen
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.COLORES_SERVICIO
import com.dusky.streamingmanager.util.dinero
import kotlinx.coroutines.launch

/**
 * Crear o editar un proveedor (nombre, color, icono, WhatsApp de ventas y soporte)
 * y administrar sus servicios.
 */
@Composable
fun EditorProveedorScreen(
    vm: MainViewModel,
    proveedorId: Long?,
    irA: (Pantalla) -> Unit,
    alVolver: () -> Unit,
) {
    val proveedores by vm.proveedores.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val existente = proveedorId?.let { id -> proveedores.find { it.id == id } }

    var nombre by remember { mutableStateOf(existente?.nombre ?: "") }
    var color by remember { mutableIntStateOf(existente?.color ?: COLORES_SERVICIO[6]) }
    var icono by remember { mutableStateOf(existente?.icono) }
    var ventas by remember { mutableStateOf(existente?.whatsappVentas ?: "") }
    var soporte by remember { mutableStateOf(existente?.whatsappSoporte ?: "") }
    var confirmarEliminar by remember { mutableStateOf(false) }

    // El primer proveedor ("Default") no se puede eliminar
    val esElPrimero = existente != null && proveedores.firstOrNull()?.id == existente.id

    PantallaConTitulo(
        titulo = if (existente == null) "Nuevo proveedor" else "Proveedor",
        alVolver = alVolver,
    ) {
        Tarjeta {
            CampoTexto(nombre, { nombre = it }, "Nombre *")
            Subtitulo("Color")
            SelectorColor(color) { color = it }
            Subtitulo("Icono (opcional)")
            SelectorImagen(icono, color, nombre) { icono = it }
            CampoTexto(ventas, { ventas = it }, "WhatsApp de ventas (opcional)", teclado = KeyboardType.Phone)
            CampoTexto(soporte, { soporte = it }, "WhatsApp de soporte (opcional)", teclado = KeyboardType.Phone)
        }

        BotonPrincipal("Guardar", habilitado = nombre.isNotBlank()) {
            scope.launch {
                val base = existente ?: Proveedor(nombre = "", color = 0)
                val id = vm.guardarProveedor(
                    base.copy(
                        nombre = nombre.trim(),
                        color = color,
                        icono = icono,
                        whatsappVentas = ventas.trim().ifBlank { null },
                        whatsappSoporte = soporte.trim().ifBlank { null },
                    ),
                )
                // Un proveedor nuevo se abre de nuevo ya guardado para poder agregarle servicios
                if (existente == null) irA(Pantalla.EditorProveedor(id)) else alVolver()
            }
        }

        if (existente != null) {
            Tarjeta {
                Subtitulo("Servicios de ${existente.nombre}")
                val propios = servicios.filter { it.proveedorId == existente.id }
                if (propios.isEmpty()) {
                    Text("Todavía no hay servicios. Agrega el primero.", color = Paleta.TextoSecundario)
                }
                propios.forEach { sv ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { irA(Pantalla.EditorServicio(existente.id, sv.id)) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icono(sv.icono, sv.color, sv.nombre)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sv.nombre, color = Paleta.Texto, fontWeight = FontWeight.Bold)
                            Text(
                                (if (sv.esPerfil) "Perfil" else "Cuenta") +
                                    " · costo ${dinero(sv.costo)} · venta ${dinero(sv.precio)}",
                                color = Paleta.TextoSecundario,
                            )
                        }
                        Text("›", color = Paleta.TextoSecundario)
                    }
                }
                BotonPrincipal("+ Nuevo servicio") { irA(Pantalla.EditorServicio(existente.id, null)) }
            }

            if (!esElPrimero) {
                BotonPeligro("Eliminar proveedor") { confirmarEliminar = true }
            }
        }
    }

    if (confirmarEliminar && existente != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar ${existente.nombre}?") },
            text = { Text("Se eliminan también sus servicios. Las suscripciones de tus clientes no se borran.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarProveedor(existente)
                    alVolver()
                }) { Text("Sí, eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } },
        )
    }
}
