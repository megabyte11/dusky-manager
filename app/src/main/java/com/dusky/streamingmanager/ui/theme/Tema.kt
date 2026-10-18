package com.dusky.streamingmanager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Todos los colores de la app en un solo lugar. Cambia aquí y cambia en toda la app. */
object Paleta {
    val Fondo = Color(0xFF0D1016)
    val Superficie = Color(0xFF161B22)
    val SuperficieAlta = Color(0xFF1E252E)
    val Borde = Color(0xFF30363D)
    val Texto = Color(0xFFE6EDF3)
    val TextoSecundario = Color(0xFF8B949E)

    val Verde = Color(0xFF3FB950)
    val Rojo = Color(0xFFF85149)
    val Amarillo = Color(0xFFE3B341)
    val Azul = Color(0xFF58A6FF)
    val Morado = Color(0xFFBC8CFF)
    val Gris = Color(0xFF6B7280)
    val WhatsApp = Color(0xFF25D366)
}

private val EsquemaOscuro = darkColorScheme(
    primary = Paleta.Azul,
    onPrimary = Color.Black,
    background = Paleta.Fondo,
    onBackground = Paleta.Texto,
    surface = Paleta.Superficie,
    onSurface = Paleta.Texto,
    onSurfaceVariant = Paleta.TextoSecundario,
    surfaceContainer = Paleta.Superficie,
    surfaceContainerHigh = Paleta.SuperficieAlta,
    secondaryContainer = Paleta.SuperficieAlta,
    onSecondaryContainer = Paleta.Texto,
    outline = Paleta.Borde,
    outlineVariant = Paleta.Borde,
)

/** Envuelve toda la app con el tema oscuro (no hay tema claro a propósito). */
@Composable
fun DuskyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaOscuro, content = content)
}
