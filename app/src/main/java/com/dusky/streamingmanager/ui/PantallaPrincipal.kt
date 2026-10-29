package com.dusky.streamingmanager.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.ui.ajustes.AjustesScreen
import com.dusky.streamingmanager.ui.clientes.ClientesScreen
import com.dusky.streamingmanager.ui.clientes.DialogoRenovar
import com.dusky.streamingmanager.ui.historial.HistorialScreen
import com.dusky.streamingmanager.ui.theme.Paleta
import kotlinx.coroutines.launch

/**
 * Pantalla con las 3 pestañas abajo y el botón redondo:
 *  - en Clientes: "+" para agregar (se vuelve basurero rojo si hay clientes seleccionados),
 *  - la selección se hace manteniendo pulsada una tarjeta.
 */
@Composable
fun PantallaPrincipal(
    vm: MainViewModel,
    pestana: Int,
    alCambiarPestana: (Int) -> Unit,
    irA: (Pantalla) -> Unit,
) {
    var seleccion by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var renovando by remember { mutableStateOf<Suscripcion?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val servicios by vm.servicios.collectAsStateWithLifecycle()

    // "Atrás" cancela la selección
    BackHandler(enabled = seleccion.isNotEmpty()) { seleccion = emptySet() }

    /** Elimina los seleccionados y ofrece "Deshacer" unos segundos. */
    fun eliminarSeleccion() {
        val lista = vm.suscripciones.value.filter { it.id in seleccion }
        seleccion = emptySet()
        if (lista.isEmpty()) return
        vm.eliminar(lista)
        scope.launch {
            val mensaje = if (lista.size == 1) "Cliente eliminado" else "${lista.size} clientes eliminados"
            val resultado = snackbar.showSnackbar(
                message = mensaje,
                actionLabel = "Deshacer",
                duration = SnackbarDuration.Long,
            )
            if (resultado == SnackbarResult.ActionPerformed) vm.restaurar(lista)
        }
    }

    Scaffold(
        containerColor = Paleta.Fondo,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            BarraPestanas(seleccionada = pestana) {
                alCambiarPestana(it)
                seleccion = emptySet()
            }
        },
        floatingActionButton = {
            if (pestana == 0) {
                val borrando = seleccion.isNotEmpty()
                FloatingActionButton(
                    onClick = { if (borrando) eliminarSeleccion() else irA(Pantalla.Formulario(null)) },
                    shape = CircleShape,
                    containerColor = if (borrando) Paleta.Rojo else Paleta.Verde,
                    contentColor = Color.Black,
                ) {
                    Icon(
                        imageVector = if (borrando) Icons.Filled.Delete else Icons.Filled.Add,
                        contentDescription = if (borrando) "Eliminar seleccionados" else "Agregar cliente",
                    )
                }
            }
        },
    ) { margenes ->
        Box(Modifier.padding(margenes).fillMaxSize()) {
            when (pestana) {
                0 -> ClientesScreen(
                    vm = vm,
                    seleccion = seleccion,
                    alAlternarSeleccion = { id ->
                        seleccion = if (id in seleccion) seleccion - id else seleccion + id
                    },
                    alRenovar = { renovando = it },
                    alEditar = { irA(Pantalla.Formulario(it.id)) },
                    alOtroServicio = { irA(Pantalla.Formulario(null, it.id)) },
                )
                1 -> HistorialScreen(vm)
                else -> AjustesScreen(vm, irA)
            }
        }
    }

    renovando?.let { s ->
        DialogoRenovar(
            s = s,
            diasPorDefecto = servicios.find { it.id == s.servicioId }?.diasPorDefecto ?: 30,
            alCancelar = { renovando = null },
            alConfirmar = { dias, precio ->
                vm.renovar(s, dias, precio)
                renovando = null
            },
        )
    }
}

@Composable
private fun BarraPestanas(seleccionada: Int, alCambiar: (Int) -> Unit) {
    val pestanas = listOf(
        "Clientes" to Icons.Filled.Person,
        "Historial" to Icons.Filled.DateRange,
        "Ajustes" to Icons.Filled.Settings,
    )
    NavigationBar(containerColor = Paleta.Superficie) {
        pestanas.forEachIndexed { indice, (nombre, icono) ->
            NavigationBarItem(
                selected = seleccionada == indice,
                onClick = { alCambiar(indice) },
                icon = { Icon(icono, contentDescription = nombre) },
                label = { Text(nombre) },
            )
        }
    }
}
