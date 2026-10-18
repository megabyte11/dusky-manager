package com.dusky.streamingmanager.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dusky.streamingmanager.ui.ajustes.EditorProveedorScreen
import com.dusky.streamingmanager.ui.ajustes.EditorServicioScreen
import com.dusky.streamingmanager.ui.ajustes.MensajesScreen
import com.dusky.streamingmanager.ui.ajustes.NotificacionesScreen
import com.dusky.streamingmanager.ui.clientes.FormularioScreen

/**
 * Raíz de la app: decide qué pantalla se ve y maneja el botón "atrás".
 * Pestañas de la pantalla principal: 0 = Clientes, 1 = Historial, 2 = Ajustes.
 */
@Composable
fun App() {
    val vm: MainViewModel = viewModel()
    var pantalla by remember { mutableStateOf<Pantalla>(Pantalla.Principal) }
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    // "Atrás": desde un editor de servicio se vuelve al proveedor; desde lo demás, a la principal.
    fun volver() {
        val actual = pantalla
        pantalla = if (actual is Pantalla.EditorServicio) {
            Pantalla.EditorProveedor(actual.proveedorId)
        } else {
            Pantalla.Principal
        }
    }
    BackHandler(enabled = pantalla != Pantalla.Principal) { volver() }

    // Pide permiso para notificaciones (Android 13+) una sola vez, al abrir la app por primera vez.
    val config by vm.config.collectAsStateWithLifecycle()
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33 && !config.permisoPreguntado) {
            vm.marcarPermisoPreguntado()
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // key(pantalla): cada pantalla empieza con su estado limpio.
    key(pantalla) {
        when (val p = pantalla) {
            Pantalla.Principal -> PantallaPrincipal(
                vm = vm,
                pestana = pestana,
                alCambiarPestana = { pestana = it },
                irA = { pantalla = it },
            )
            is Pantalla.Formulario -> FormularioScreen(vm, p.suscripcionId) { volver() }
            is Pantalla.EditorProveedor -> EditorProveedorScreen(
                vm = vm,
                proveedorId = p.proveedorId,
                irA = { pantalla = it },
                alVolver = { volver() },
            )
            is Pantalla.EditorServicio -> EditorServicioScreen(vm, p.proveedorId, p.servicioId) { volver() }
            Pantalla.Mensajes -> MensajesScreen(vm) { volver() }
            Pantalla.Notificaciones -> NotificacionesScreen(vm) { volver() }
        }
    }
}
