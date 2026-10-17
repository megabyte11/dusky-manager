package com.dusky.streamingmanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dusky.streamingmanager.data.AppDatabase
import com.dusky.streamingmanager.data.Movimiento
import com.dusky.streamingmanager.data.Repositorio
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.util.Resumen
import com.dusky.streamingmanager.util.calcularResumen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * El "cerebro" de las pantallas: lee la base de datos y le entrega a la interfaz
 * datos listos para mostrar. Sobrevive a rotaciones de pantalla.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = Repositorio(AppDatabase.obtener(app))

    /** Suscripciones activas (la lista principal). */
    val suscripciones: StateFlow<List<Suscripcion>> = repositorio.suscripcionesActivas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Historial de renovaciones, vencimientos y cancelaciones. */
    val movimientos: StateFlow<List<Movimiento>> = repositorio.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Números de las tarjetas de arriba. */
    val resumen: StateFlow<Resumen> =
        combine(repositorio.suscripcionesActivas, repositorio.movimientos) { activas, movs ->
            calcularResumen(activas, movs)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Resumen())

    init {
        viewModelScope.launch { repositorio.asegurarProveedorDefault() }
    }

    /** SOLO PARA PROBAR (Fase 1). Se reemplaza por el formulario real en la Fase 2. */
    fun agregarPruebas() {
        viewModelScope.launch { repositorio.agregarSuscripcionesDePrueba() }
    }
}
