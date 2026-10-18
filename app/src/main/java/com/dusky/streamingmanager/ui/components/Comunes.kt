package com.dusky.streamingmanager.ui.components

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dusky.streamingmanager.ui.theme.Paleta
import com.dusky.streamingmanager.util.COLORES_SERVICIO
import com.dusky.streamingmanager.util.guardarIcono

/*
 * Piezas reutilizables de la interfaz: iconos, campos de texto, selectores, botones y el
 * marco de las pantallas secundarias.
 */

/** Círculo con la imagen del icono, o con la inicial del nombre sobre el color si no hay imagen. */
@Composable
fun Icono(
    ruta: String?,
    colorFondo: Int,
    inicial: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 36.dp,
) {
    val imagen = remember(ruta) { ruta?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() } }
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(Color(colorFondo)),
        contentAlignment = Alignment.Center,
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                text = inicial.trim().take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/** Campo de texto de una línea (o varias si [lineas] > 1). */
@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1,
    ayuda: String? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = lineas == 1,
        minLines = lineas,
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        supportingText = if (ayuda != null) ({ Text(ayuda) }) else null,
    )
}

/** Campo que al tocarlo despliega una lista de opciones. [alElegir] recibe la posición elegida. */
@Composable
fun CampoSeleccion(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    alElegir: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
        )
        // Capa transparente encima: captura el toque para abrir el menú
        Box(Modifier.matchParentSize().clickable { abierto = true })
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEachIndexed { indice, texto ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        abierto = false
                        alElegir(indice)
                    },
                )
            }
        }
    }
}

/** Paleta de 12 colores para elegir uno. */
@Composable
fun SelectorColor(actual: Int, alElegir: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        COLORES_SERVICIO.chunked(6).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                fila.forEach { color ->
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(
                                if (color == actual) Modifier.border(3.dp, Color.White, CircleShape) else Modifier,
                            )
                            .clickable { alElegir(color) },
                    )
                }
            }
        }
    }
}

/** Vista previa del icono + botón para elegir una imagen de la galería (sin pedir permisos). */
@Composable
fun SelectorImagen(
    ruta: String?,
    colorFondo: Int,
    inicial: String,
    alCambiar: (String?) -> Unit,
) {
    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) alCambiar(guardarIcono(contexto, uri))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icono(ruta, colorFondo, inicial, tamano = 56.dp)
        OutlinedButton(
            onClick = {
                lanzador.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        ) { Text("Elegir imagen") }
        if (ruta != null) {
            OutlinedButton(onClick = { alCambiar(null) }) { Text("Quitar") }
        }
    }
}

/** Selector numérico simple: [−] valor [+]. */
@Composable
fun Paso(etiqueta: String, valor: String, alBajar: () -> Unit, alSubir: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(etiqueta, color = Paleta.Texto)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = alBajar) { Text("−") }
            Text(valor, color = Paleta.Texto, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = alSubir) { Text("+") }
        }
    }
}

/** Botón grande azul. */
@Composable
fun BotonPrincipal(texto: String, modifier: Modifier = Modifier, habilitado: Boolean = true, alTocar: () -> Unit) {
    Button(
        onClick = alTocar,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Paleta.Azul, contentColor = Color.Black),
    ) { Text(texto, fontWeight = FontWeight.Bold) }
}

/** Botón rojo con borde, para acciones que borran o cancelan. */
@Composable
fun BotonPeligro(texto: String, modifier: Modifier = Modifier, alTocar: () -> Unit) {
    OutlinedButton(
        onClick = alTocar,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Paleta.Rojo),
    ) { Text(texto, fontWeight = FontWeight.Bold) }
}

/** Caja con fondo de tarjeta para agrupar contenido. */
@Composable
fun Tarjeta(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Paleta.Superficie)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = contenido,
    )
}

@Composable
fun Subtitulo(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleSmall, color = Paleta.TextoSecundario)
}

/** Marco de las pantallas secundarias: flecha para volver, título y contenido con scroll. */
@Composable
fun PantallaConTitulo(
    titulo: String,
    alVolver: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Paleta.Fondo)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = alVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Paleta.Texto)
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Paleta.Texto,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido,
        )
    }
}
