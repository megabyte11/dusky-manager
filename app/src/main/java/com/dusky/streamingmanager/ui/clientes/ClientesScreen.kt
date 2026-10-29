package com.dusky.streamingmanager.ui.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.AccionesTarjeta
import com.dusky.streamingmanager.ui.components.PanelResumen
import com.dusky.streamingmanager.ui.components.TarjetaSuscripcion
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.abrirWhatsApp
import com.dusky.streamingmanager.util.claveCliente
import com.dusky.streamingmanager.util.enviarPorWhatsApp
import com.dusky.streamingmanager.util.mensajeDatos
import com.dusky.streamingmanager.util.mensajeRecordatorio

/**
 * Pestaña principal: tarjetas de resumen fijas arriba y la lista de suscripciones debajo.
 * Tocar una tarjeta la expande; mantenerla pulsada la selecciona (para eliminar).
 */
@Composable
fun ClientesScreen(
    vm: MainViewModel,
    seleccion: Set<Long>,
    alAlternarSeleccion: (Long) -> Unit,
    alRenovar: (Suscripcion) -> Unit,
    alEditar: (Suscripcion) -> Unit,
    alOtroServicio: (Suscripcion) -> Unit,
) {
    val contexto = LocalContext.current
    val resumen by vm.resumen.collectAsStateWithLifecycle()
    val lista by vm.suscripciones.collectAsStateWithLifecycle()
    val proveedores by vm.proveedores.collectAsStateWithLifecycle()
    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val config by vm.config.collectAsStateWithLifecycle()

    var expandidoId by rememberSaveable { mutableStateOf<Long?>(null) }
    val enSeleccion = seleccion.isNotEmpty()

    Column(Modifier.fillMaxSize()) {
        PanelResumen(resumen, Modifier.padding(16.dp))

        if (enSeleccion) {
            Text(
                text = "${seleccion.size} seleccionado(s) · toca el botón rojo para eliminar",
                color = Paleta.Amarillo,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            // 88dp abajo para que el botón redondo no tape la última tarjeta
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (lista.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay clientes. Toca + para agregar el primero.",
                        color = Paleta.TextoSecundario,
                    )
                }
            }
            items(lista, key = { it.id }) { s ->
                val servicio = servicios.find { it.id == s.servicioId }
                val proveedor = proveedores.find { it.id == s.proveedorId }
                val expandida = expandidoId == s.id && !enSeleccion

                TarjetaSuscripcion(
                    s = s,
                    icono = servicio?.icono,
                    proveedor = proveedor,
                    seleccionada = s.id in seleccion,
                    alTocar = {
                        if (enSeleccion) {
                            alAlternarSeleccion(s.id)
                        } else {
                            expandidoId = if (expandidoId == s.id) null else s.id
                        }
                    },
                    alMantener = { if (!enSeleccion) alAlternarSeleccion(s.id) },
                    acciones = if (expandida) {
                        AccionesTarjeta(
                            recordar = {
                                // Misma persona y misma fecha de vencimiento -> un solo mensaje combinado
                                val clave = claveCliente(s.telefono, s.usuarioWhatsApp)
                                val grupo = lista.filter { otra ->
                                    claveCliente(otra.telefono, otra.usuarioWhatsApp) == clave &&
                                        otra.fechaVencimiento == s.fechaVencimiento
                                }
                                enviarPorWhatsApp(
                                    contexto, s.telefono, s.usuarioWhatsApp, config.codigoPais,
                                    mensajeRecordatorio(s, grupo, config),
                                )
                            },
                            enviarDatos = {
                                enviarPorWhatsApp(
                                    contexto, s.telefono, s.usuarioWhatsApp, config.codigoPais,
                                    mensajeDatos(s, config),
                                )
                            },
                            renovar = { alRenovar(s) },
                            editar = { alEditar(s) },
                            otroServicio = { alOtroServicio(s) },
                            chatVentas = proveedor?.whatsappVentas?.takeIf { it.isNotBlank() }?.let { numero ->
                                { abrirWhatsApp(contexto, numero, config.codigoPais) }
                            },
                            chatSoporte = proveedor?.whatsappSoporte?.takeIf { it.isNotBlank() }?.let { numero ->
                                { abrirWhatsApp(contexto, numero, config.codigoPais) }
                            },
                        )
                    } else {
                        null
                    },
                )
            }
        }
    }
}
