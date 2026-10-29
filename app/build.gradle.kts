// Configuración del módulo "app": versión de Android, firma, y librerías que usa.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

// Llave de firma PROPIA: la entrega GitHub Actions con los secretos del repositorio
// (ver .github/workflows/build-apk.yml). Si no existe (por ejemplo, al compilar un fork),
// se firma con la llave de pruebas del proyecto y la app igual se compila.
val rutaLlave: String? = System.getenv("KEYSTORE_FILE")

android {
    namespace = "com.dusky.streamingmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dusky.streamingmanager"
        minSdk = 26          // Android 8.0 en adelante
        targetSdk = 35
        versionCode = 4      // sube 1 con cada versión nueva
        versionName = "1.1.0"
    }

    signingConfigs {
        // Llave de pruebas pública (app/debug.keystore). NO sirve para publicar de verdad.
        getByName("debug") {
            storeFile = file("debug.keystore")
            storeType = "pkcs12"
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        // Tu llave privada (solo existe cuando compila GitHub con los secretos cargados)
        if (rutaLlave != null) {
            create("release") {
                storeFile = file(rutaLlave)
                storeType = "pkcs12"
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // Versión final: código recortado y más liviana.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = if (rutaLlave != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true   // permite leer la versión desde el código (BuildConfig.VERSION_NAME)
    }

    // No frenar la compilación release por avisos del analizador (lint).
    lint {
        checkReleaseBuilds = false
    }

    // No incluir en el APK el bloque cifrado de "información de dependencias" de Google.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    // Compose (interfaz). El "BOM" mantiene todas las versiones de Compose alineadas.
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")

    // Base de Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // WorkManager: tarea diaria que revisa los vencimientos (sin servicios permanentes)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Room: base de datos local (SQLite)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}
