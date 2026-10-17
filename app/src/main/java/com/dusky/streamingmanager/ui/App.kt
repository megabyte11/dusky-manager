package com.dusky.streamingmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dusky.streamingmanager.ui.ajustes.AjustesScreen
import com.dusky.streamingmanager.ui.clientes.ClientesScreen
import com.dusky.streamingmanager.ui.historial.HistorialScreen
import com.dusky.streamingmanager.ui.theme.Paleta

/**
 * Estructura general: pantalla actual + barra de 3 pestañas abajo + botón redondo (+).
 * Pestañas: 0 = Clientes, 1 = Historial, 2 = Ajustes.
 */
@Composable
fun App() {
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    val vm: MainViewModel = viewModel()

    Scaffold(
        containerColor = Paleta.Fondo,
        bottomBar = { BarraPestanas(seleccionada = pestana, alCambiar = { pestana = it }) },
        floatingActionButton = {
            if (pestana == 0) {
                FloatingActionButton(
                    onClick = vm::agregarPruebas,   // Fase 2: abrirá el formulario de nuevo cliente
                    shape = CircleShape,
                    containerColor = Paleta.Verde,
                    contentColor = Color.Black,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar cliente")
                }
            }
        },
    ) { margenes ->
        Box(Modifier.padding(margenes).fillMaxSize()) {
            when (pestana) {
                0 -> ClientesScreen(vm)
                1 -> HistorialScreen(vm)
                else -> AjustesScreen()
            }
        }
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
