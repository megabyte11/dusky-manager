package com.dusky.streamingmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dusky.streamingmanager.ui.App
import com.dusky.streamingmanager.ui.theme.DuskyTheme

/**
 * Punto de entrada de la app. Solo prepara la pantalla (tema oscuro, barras del sistema
 * transparentes) y le entrega el control a la función [App].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // La app es siempre oscura: iconos claros en las barras de estado y navegación.
        val transparente = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(transparente),
            navigationBarStyle = SystemBarStyle.dark(transparente),
        )

        setContent {
            DuskyTheme {
                App()
            }
        }
    }
}
