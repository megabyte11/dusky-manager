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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dusky.streamingmanager.ui.MainViewModel
import com.dusky.streamingmanager.ui.components.PanelResumen
import com.dusky.streamingmanager.ui.components.TarjetaSuscripcion
import com.dusky.streamingmanager.ui.theme.Paleta

/** Pestaña principal: tarjetas de resumen fijas arriba y la lista de suscripciones debajo. */
@Composable
fun ClientesScreen(vm: MainViewModel) {
    val resumen by vm.resumen.collectAsStateWithLifecycle()
    val lista by vm.suscripciones.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        PanelResumen(resumen, Modifier.padding(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            // 88dp abajo para que el botón (+) no tape la última tarjeta
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (lista.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay clientes. Toca + para agregar datos de prueba.",
                        color = Paleta.TextoSecundario,
                    )
                }
            }
            items(lista, key = { it.id }) { suscripcion ->
                TarjetaSuscripcion(suscripcion)
            }
        }
    }
}
