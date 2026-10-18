package com.dusky.streamingmanager.ui.clientes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.dusky.streamingmanager.util.colorSugerido
import com.dusky.streamingmanager.util.formatearFecha
import com.dusky.streamingmanager.util.hoy
import com.dusky.streamingmanager.util.numeroATexto
import com.dusky.streamingmanager.util.parsearFecha
import com.dusky.streamingmanager.util.soloDigitos
import com.dusky.streamingmanager.util.textoANumero

private const val OPCION_OTRO = "Otro (escribir)"

/**
 * Formulario para agregar un cliente nuevo ([suscripcionId] = null) o editar uno existente.
 * Al elegir un servicio del proveedor, el costo, el precio y los días se rellenan solos.
 */
@Composable
fun FormularioScreen(vm: MainViewModel, suscripcionId: Long?, alVolver: () -> Unit) {
    val suscripciones by vm.suscripciones.collectAsStateWithLifecycle()
    val proveedores by vm.proveedores.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val conocidos by vm.clientesConocidos.collectAsStateWithLifecycle()

    val existente = suscripcionId?.let { id -> suscripciones.find { it.id == id } }

    var nombre by remember { mutableStateOf(existente?.nombreCliente ?: "") }
    var telefono by remember { mutableStateOf(existente?.telefono ?: "") }
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

    PantallaConTitulo(
        titulo = if (existente == null) "Nuevo cliente" else "Editar cliente",
        alVolver = alVolver,
    ) {
        Tarjeta {
            Subtitulo("Cliente")
            CampoTexto(nombre, { nombre = it }, "Nombre *")
            CampoTexto(
                valor = telefono,
                alCambiar = { nuevo ->
                    telefono = nuevo
                    // Si ya conocemos ese teléfono, se rellena el nombre
                    if (nombre.isBlank() && soloDigitos(nuevo).length >= 7) {
                        conocidos.find { soloDigitos(it.telefono) == soloDigitos(nuevo) }
                            ?.let { nombre = it.nombreCliente }
                    }
                },
                etiqueta = "Teléfono *",
                teclado = KeyboardType.Phone,
            )
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
                            if (existente == null) vencimiento = formatearFecha(hoy() + elegido.diasPorDefecto)
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
            if (existente == null) {
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
                telefono.isBlank() -> "Escribe el teléfono del cliente."
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
                        telefono = telefono.trim(),
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
