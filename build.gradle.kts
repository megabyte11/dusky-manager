// Archivo raíz: solo declara las versiones de los plugins.
// Las versiones están fijadas a propósito (probadas juntas). Se actualizan al final del proyecto.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
}
