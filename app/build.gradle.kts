// Configuración del módulo "app": versión de Android, firma, y librerías que usa.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.dusky.streamingmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dusky.streamingmanager"
        minSdk = 26          // Android 8.0 en adelante
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
    }

    // Llave de firma FIJA (app/debug.keystore). Sin esto, cada compilación en GitHub
    // saldría con una firma distinta y Android no dejaría actualizar encima de la anterior.
    // Es una llave de pruebas pública: NO sirve para publicar en Play Store.
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storeType = "pkcs12"
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            // Versión final: código recortado y más liviano.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
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
