package com.dusky.streamingmanager.ui

import android.app.Application
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dusky.streamingmanager.data.AppDatabase
import com.dusky.streamingmanager.data.ClienteConocido
import com.dusky.streamingmanager.data.Config
import com.dusky.streamingmanager.data.ConfigStore
import com.dusky.streamingmanager.data.Movimiento
import com.dusky.streamingmanager.data.Proveedor
import com.dusky.streamingmanager.data.Repositorio
import com.dusky.streamingmanager.data.Respaldo
import com.dusky.streamingmanager.data.Servicio
import com.dusky.streamingmanager.data.Suscripcion
import com.dusky.streamingmanager.notificaciones.Notificador
import com.dusky.streamingmanager.notificaciones.Programador
import com.dusky.streamingmanager.util.Resumen
import com.dusky.streamingmanager.util.calcularResumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * El "cerebro" de las pantallas: lee la base de datos y le entrega a la interfaz datos listos
 * para mostrar, y recibe las acciones del usuario (guardar, renovar, eliminar...).
 * Sobrevive a rotaciones de pantalla.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val bd = AppDatabase.obtener(app)
    private val repositorio = Repositorio(bd)
    private val almacenConfig = ConfigStore(app)

    private fun <T> Flow<T>.comoEstado(inicial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), inicial)

    // ---------- Datos para las pantallas ----------

    val suscripciones: StateFlow<List<Suscripcion>> = repositorio.suscripcionesActivas.comoEstado(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = repositorio.movimientos.comoEstado(emptyList())
    val proveedores: StateFlow<List<Proveedor>> = repositorio.proveedores.comoEstado(emptyList())
    val servicios: StateFlow<List<Servicio>> = repositorio.servicios.comoEstado(emptyList())
    val clientesConocidos: StateFlow<List<ClienteConocido>> = repositorio.clientesConocidos.comoEstado(emptyList())

    val resumen: StateFlow<Resumen> =
        combine(repositorio.suscripcionesActivas, repositorio.movimientos) { activas, movs ->
            calcularResumen(activas, movs)
        }.comoEstado(Resumen())

    private val _config = MutableStateFlow(almacenConfig.leer())
    val config: StateFlow<Config> = _config.asStateFlow()

    init {
        viewModelScope.launch { repositorio.asegurarProveedorDefault() }
    }

    // ---------- Suscripciones ----------

    fun guardarSuscripcion(s: Suscripcion) {
        viewModelScope.launch { repositorio.guardarSuscripcion(s) }
    }

    fun renovar(s: Suscripcion, dias: Int, precio: Double) {
        viewModelScope.launch { repositorio.renovar(s, dias, precio) }
    }

    fun cancelar(s: Suscripcion) {
        viewModelScope.launch { repositorio.cancelar(s) }
    }

    fun eliminar(lista: List<Suscripcion>) {
        viewModelScope.launch { repositorio.eliminar(lista) }
    }

    fun restaurar(lista: List<Suscripcion>) {
        viewModelScope.launch { repositorio.restaurar(lista) }
    }

    // ---------- Proveedores y servicios ----------

    suspend fun guardarProveedor(p: Proveedor): Long = repositorio.guardarProveedor(p)

    fun eliminarProveedor(p: Proveedor) {
        viewModelScope.launch { repositorio.eliminarProveedor(p) }
    }

    suspend fun contarSuscripcionesDeServicio(servicioId: Long): Int =
        repositorio.contarSuscripcionesDeServicio(servicioId)

    fun guardarServicio(sv: Servicio, actualizarSuscripciones: Boolean) {
        viewModelScope.launch { repositorio.guardarServicio(sv, actualizarSuscripciones) }
    }

    fun eliminarServicio(sv: Servicio) {
        viewModelScope.launch { repositorio.eliminarServicio(sv) }
    }

    // ---------- Ajustes y notificaciones ----------

    /** Guarda los ajustes y reprograma el aviso diario si hace falta. */
    fun guardarConfig(nueva: Config) {
        val anterior = _config.value
        _config.value = nueva
        almacenConfig.guardar(nueva)

        val contexto = getApplication<Application>()
        if (nueva.notifActivas) {
            val cambioHora = anterior.notifHora != nueva.notifHora || !anterior.notifActivas
            Programador.programar(contexto, nueva.notifHora, reemplazar = cambioHora)
        } else {
            Programador.cancelar(contexto)
        }
    }

    fun marcarPermisoPreguntado() {
        guardarConfig(_config.value.copy(permisoPreguntado = true))
    }

    fun notificacionesPermitidas(): Boolean =
        NotificationManagerCompat.from(getApplication<Application>()).areNotificationsEnabled()

    /** Muestra una notificación de prueba ahora mismo. */
    fun probarNotificacion() {
        viewModelScope.launch {
            val activas = bd.suscripcionDao().activas()
            Notificador.mostrar(getApplication<Application>(), activas, _config.value, siempre = true)
        }
    }

    // ---------- Respaldo ----------

    suspend fun exportar(destino: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val texto = Respaldo.exportar(bd)
            val salida = getApplication<Application>().contentResolver.openOutputStream(destino)
                ?: return@withContext false
            salida.use { it.write(texto.toByteArray(Charsets.UTF_8)) }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun importar(origen: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val entrada = getApplication<Application>().contentResolver.openInputStream(origen)
                ?: return@withContext false
            val texto = entrada.bufferedReader(Charsets.UTF_8).use { it.readText() }
            Respaldo.importar(bd, texto)
            true
        } catch (e: Exception) {
            false
        }
    }
}
