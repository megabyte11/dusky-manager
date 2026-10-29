package com.dusky.streamingmanager.ui.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.ClienteConocido
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.BotonPeligro
import com.dusky.streamingmanager.ui.components.BotonPrincipal
import com.dusky.streamingmanager.ui.components.CampoSeleccion
import com.dusky.streamingmanager.ui.components.CampoTexto
import com.dusky.streamingmanager.ui.components.PantallaConTitulo
import com.dusky.streamingmanager.ui.components.Subtitulo
import com.dusky.streamingmanager.ui.components.Tarjeta
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.claveCliente
import com.dusky.streamingmanager.util.colorSugerido
import com.dusky.streamingmanager.util.etiquetaContacto
import com.dusky.streamingmanager.util.formatearFecha
import com.dusky.streamingmanager.util.hoy
import com.dusky.streamingmanager.util.limpiarTelefono
import com.dusky.streamingmanager.util.limpiarUsuario
import com.dusky.streamingmanager.util.numeroATexto
import com.dusky.streamingmanager.util.parsearFecha
import com.dusky.streamingmanager.util.sugerirClientes
import com.dusky.streamingmanager.util.textoANumero
import com.dusky.streamingmanager.util.usuarioValido

private const val OPCION_OTRO = "Otro (escribir)"

/**
 * Formulario para agregar un cliente nuevo ([suscripcionId] = null) o editar uno existente.
 * Si [copiarDeId] tiene valor ("+ Otro servicio"), el nombre y el contacto del cliente vienen ya escritos.
 * Al elegir un servicio del proveedor, el costo, el precio y los días se rellenan solos.
 */
@Composable
fun FormularioScreen(vm: MainViewModel, suscripcionId: Long?, copiarDeId: Long?, alVolver: () -> Unit) {
    val suscripciones by vm.suscripciones.collectAsStateWithLifecycle()
    val proveedores by vm.proveedores.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val conocidos by vm.clientesConocidos.collectAsStateWithLifecycle()
    val config by vm.config.collectAsStateWithLifecycle()

    val existente = suscripcionId?.let { id -> suscripciones.find { it.id == id } }
    // "+ Otro servicio": la suscripción del cliente al que se le agrega otro servicio
    val origen = if (existente == null) copiarDeId?.let { id -> suscripciones.find { it.id == id } } else null
    val datosCliente = existente ?: origen

    var nombre by remember { mutableStateOf(datosCliente?.nombreCliente ?: "") }
    var telefono by remember { mutableStateOf(datosCliente?.telefono ?: "") }
    var usuario by remember { mutableStateOf(datosCliente?.usuarioWhatsApp ?: "") }
    var igualarFecha by remember { mutableStateOf(false) }
    var proveedor by remember {
        mutableStateOf(proveedores.find { it.id == existente?.proveedorId } ?: proveedores.firstOrNull())
    }
    var servicio by remember { mutableStateOf(servicios.find { it.id == existente?.servicioId }) }
    var servicioManual by remember {
        mutableStateOf(if (existente != null && servicio == null) existente.servicioNombre else "")
    }
    var costo by remember { mutableStateOf(existente?.let { numeroATexto(it.costo) } ?: "") }
    var precio by remember { mutableStateOf(existente?.let { numeroATexto(it.precio) } ?: "") }
    var dias by remember { mutableStateOf("30") }
    var vencimiento by remember {
        mutableStateOf(formatearFecha(existente?.fechaVencimiento ?: (hoy() + 30)))
    }
    var correo by remember { mutableStateOf(existente?.correo ?: "") }
    var contrasena by remember { mutableStateOf(existente?.contrasena ?: "") }
    var perfil by remember { mutableStateOf(existente?.perfil ?: "") }
    var pin by remember { mutableStateOf(existente?.pin ?: "") }
    var nota by remember { mutableStateOf(existente?.nota ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var confirmarCancelar by remember { mutableStateOf(false) }

    val serviciosDelProveedor = servicios.filter { it.proveedorId == proveedor?.id }

    // Clientes ya registrados que coinciden con lo escrito (solo al agregar uno nuevo)
    val sugerencias = if (existente == null && origen == null) {
        sugerirClientes(conocidos, nombre, telefono, usuario)
    } else {
        emptyList()
    }

    PantallaConTitulo(
        titulo = when {
            existente != null -> "Editar cliente"
            origen != null -> "Otro servicio"
            else -> "Nuevo cliente"
        },
        alVolver = alVolver,
    ) {
        Tarjeta {
            Subtitulo("Cliente")
            CampoTexto(nombre, { nombre = it }, "Nombre *")
            CampoTexto(
                valor = telefono,
                alCambiar = { nuevo ->
                    // Al pegar "+57 300 123 4567" queda "3001234567"
                    val limpio = limpiarTelefono(nuevo, config.codigoPais)
                    telefono = limpio
                    // Si ya conocemos ese teléfono, se rellena el nombre (y su usuario, si lo tiene)
                    if (nombre.isBlank() && limpio.length >= 7) {
                        conocidos.find { limpiarTelefono(it.telefono, config.codigoPais) == limpio }?.let { c ->
                            nombre = c.nombreCliente
                            if (usuario.isBlank()) usuario = c.usuarioWhatsApp.orEmpty()
                        }
                    }
                },
                etiqueta = "Teléfono",
                teclado = KeyboardType.Phone,
            )
            CampoTexto(
                valor = usuario,
                alCambiar = { usuario = limpiarUsuario(it) },
                etiqueta = "Usuario de WhatsApp (opcional)",
                ayuda = "Para clientes que te escriben desde un @usuario y no muestran su número. " +
                    "Hace falta al menos el teléfono o el usuario.",
            )
            if (sugerencias.isNotEmpty()) {
                Text(
                    text = "Ya registrados · toca para usar",
                    style = MaterialTheme.typography.labelMedium,
                    color = Paleta.TextoSecundario,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sugerencias, key = { claveCliente(it.telefono, it.usuarioWhatsApp) }) { cliente ->
                        ChipCliente(cliente) {
                            nombre = cliente.nombreCliente
                            telefono = limpiarTelefono(cliente.telefono, config.codigoPais)
                            usuario = cliente.usuarioWhatsApp.orEmpty()
                        }
                    }
                }
            }
        }

        Tarjeta {
            Subtitulo("Servicio")
            CampoSeleccion(
                etiqueta = "Proveedor",
                valor = proveedor?.nombre ?: "—",
                opciones = proveedores.map { it.nombre },
                alElegir = { i ->
                    proveedor = proveedores[i]
                    servicio = null
                },
            )
            if (serviciosDelProveedor.isNotEmpty()) {
                CampoSeleccion(
                    etiqueta = "Servicio",
                    valor = servicio?.nombre ?: OPCION_OTRO,
                    opciones = serviciosDelProveedor.map { it.nombre } + OPCION_OTRO,
                    alElegir = { i ->
                        if (i < serviciosDelProveedor.size) {
                            val elegido = serviciosDelProveedor[i]
                            servicio = elegido
                            costo = numeroATexto(elegido.costo)
                            precio = numeroATexto(elegido.precio)
                            dias = elegido.diasPorDefecto.toString()
                            if (existente == null && !igualarFecha) {
                                vencimiento = formatearFecha(hoy() + elegido.diasPorDefecto)
                            }
                        } else {
                            servicio = null
                        }
                    },
                )
            }
            if (servicio == null) {
                CampoTexto(servicioManual, { servicioManual = it }, "Nombre del servicio *")
            }
            CampoTexto(costo, { costo = it }, "Costo (lo que pagas tú) *", teclado = KeyboardType.Decimal)
            CampoTexto(precio, { precio = it }, "Venta (lo que paga el cliente) *", teclado = KeyboardType.Decimal)
            if (origen != null) {
                // Permite que los dos servicios venzan el mismo día (así el recordatorio sale combinado)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Igualar vencimiento", color = Paleta.Texto)
                        Text(
                            "Mismo día que ${origen.servicioNombre}: ${formatearFecha(origen.fechaVencimiento)}",
                            color = Paleta.TextoSecundario,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Switch(
                        checked = igualarFecha,
                        onCheckedChange = { activo ->
                            igualarFecha = activo
                            vencimiento = if (activo) {
                                formatearFecha(origen.fechaVencimiento)
                            } else {
                                formatearFecha(hoy() + (dias.toIntOrNull() ?: 30))
                            }
                        },
                    )
                }
            }
            if (existente == null && !igualarFecha) {
                CampoTexto(
                    valor = dias,
                    alCambiar = { nuevo ->
                        dias = nuevo
                        nuevo.toIntOrNull()?.let { vencimiento = formatearFecha(hoy() + it) }
                    },
                    etiqueta = "Duración (días)",
                    teclado = KeyboardType.Number,
                )
            }
            CampoTexto(vencimiento, { vencimiento = it }, "Vencimiento * (AAAA-MM-DD)")
        }

        Tarjeta {
            Subtitulo("Datos de la cuenta")
            CampoTexto(correo, { correo = it }, "Correo del servicio", teclado = KeyboardType.Email)
            CampoTexto(contrasena, { contrasena = it }, "Contraseña del servicio")
            CampoTexto(perfil, { perfil = it }, "Perfil (opcional)")
            CampoTexto(pin, { pin = it }, "PIN del perfil (opcional)")
            CampoTexto(nota, { nota = it }, "Nota (opcional)")
        }

        if (error != null) {
            Text(error ?: "", color = Paleta.Rojo)
        }

        BotonPrincipal("Guardar") {
            val nombreServicio = servicio?.nombre ?: servicioManual.trim()
            val c = textoANumero(costo)
            val p = textoANumero(precio)
            val v = parsearFecha(vencimiento)
            val problema = when {
                nombre.isBlank() -> "Escribe el nombre del cliente."
                telefono.isBlank() && usuario.isBlank() -> "Escribe el teléfono o el usuario de WhatsApp del cliente."
                usuario.isNotBlank() && !usuarioValido(usuario) ->
                    "El usuario debe tener de 3 a 35 caracteres (letras minúsculas, números, punto o guion bajo)."
                nombreServicio.isBlank() -> "Escribe el nombre del servicio."
                c == null -> "El costo no es un número válido."
                p == null -> "La venta no es un número válido."
                v == null -> "La fecha debe tener el formato AAAA-MM-DD (ej: 2026-11-16)."
                else -> null
            }
            if (problema != null) {
                error = problema
            } else {
                val base = existente ?: Suscripcion(
                    nombreCliente = "",
                    telefono = "",
                    servicioNombre = "",
                    servicioColor = 0,
                    costo = 0.0,
                    precio = 0.0,
                    fechaInicio = hoy(),
                    fechaVencimiento = 0L,
                )
                vm.guardarSuscripcion(
                    base.copy(
                        nombreCliente = nombre.trim(),
                        telefono = limpiarTelefono(telefono, config.codigoPais),
                        usuarioWhatsApp = usuario.ifBlank { null },
                        servicioId = servicio?.id,
                        proveedorId = proveedor?.id,
                        servicioNombre = nombreServicio,
                        servicioColor = servicio?.color ?: colorSugerido(nombreServicio),
                        costo = c!!,
                        precio = p!!,
                        fechaVencimiento = v!!,
                        correo = correo.trim(),
                        contrasena = contrasena,
                        perfil = perfil.trim().ifBlank { null },
                        pin = pin.trim().ifBlank { null },
                        nota = nota.trim().ifBlank { null },
                    ),
                )
                alVolver()
            }
        }

        if (existente != null) {
            BotonPeligro("Cancelar suscripción") { confirmarCancelar = true }
            Text(
                "Cancelar la pasa al Historial y borra sus datos de acceso. Para borrarla sin dejar rastro, " +
                    "mantenla pulsada en la lista.",
                color = Paleta.TextoSecundario,
            )
        }
    }

    if (confirmarCancelar && existente != null) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("¿Cancelar esta suscripción?") },
            text = { Text("Quedará registrada en el Historial y se borrarán el correo y la contraseña guardados.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelar(existente)
                    alVolver()
                }) { Text("Sí, cancelar") }
            },
            dismissButton = { TextButton(onClick = { confirmarCancelar = false }) { Text("No") } },
        )
    }
}

/** Cápsula con el nombre del cliente y los últimos 4 dígitos de su teléfono (o su @usuario). */
@Composable
private fun ChipCliente(cliente: ClienteConocido, alTocar: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Paleta.SuperficieAlta)
            .clickable(onClick = alTocar)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = cliente.nombreCliente,
            color = Paleta.Texto,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = " · " + etiquetaContacto(cliente),
            color = Paleta.TextoSecundario,
        )
    }
}
