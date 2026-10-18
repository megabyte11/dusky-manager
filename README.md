# Dusky Streaming Manager🍤

App de Android, liviana y de código abierto, para gestionar clientes con mensualidades de servicios de streaming:
vencimientos, ganancias, recordatorios por WhatsApp y notificaciones. Tema oscuro, sin internet, sin cuentas:
todos los datos se guardan solo en tu celular.

> **Estado:** v0.2.0 — app completa en pruebas.🐟

## Hecho con ayuda de IA🥀

El código de este proyecto fue escrito con ayuda de **Claude** (IA de Anthropic).
El diseño, las ideas y la dirección del proyecto son de **dusky_os**. Se comparte con licencia MIT para que
cualquiera pueda usarlo, estudiarlo y mejorarlo.

## Descargar e instalar👀

1. Entra a la pestaña **Releases** de este repositorio y abre **Última compilación**.
2. Descarga `app-debug.apk` desde tu celular.
3. Ábrelo e instálalo (Android pedirá permitir "instalar apps de esta fuente").

Cada vez que se suben cambios a la rama `main`, GitHub compila un APK nuevo automáticamente
(pestaña **Actions**, tarda unos minutos). Se puede instalar encima del anterior sin perder datos.

> El APK está firmado con una llave de pruebas pública (`app/debug.keystore`).
> Sirve para instalar y probar, **no** para publicar en Google Play.

## Tecnología🦝

- Kotlin + Jetpack Compose (interfaz) + Room (base de datos local)
- Un solo módulo, organizado por carpetas, con comentarios en español en cada archivo

```
app/src/main/java/com/dusky/streamingmanager/
├── data/            Tablas, consultas y repositorio
├── ui/
│   ├── theme/       Colores y tema oscuro
│   ├── components/  Piezas reutilizables (tarjetas)
│   ├── clientes/    Pestaña principal
│   ├── historial/   Pestaña de historial
│   └── ajustes/     Pestaña de ajustes
└── util/            Fechas, formato de dinero, cálculo del resumen
```

## Funciones🔥

- Lista de suscripciones ordenada por vencimiento, con barra de color por servicio (izquierda) y por días restantes (derecha: 0–5 rojo, 6–10 amarillo, 11+ verde).
- Tarjeta expandible con teléfono, costo, venta, ganancia, fechas, correo, contraseña, perfil, PIN y nota.
- Agregar, editar, renovar (queda en el historial), cancelar y eliminar (mantener pulsado, con "Deshacer").
- Proveedores (icono, color, WhatsApp de ventas y soporte) y servicios con costo y precio; al cambiar precios pregunta si actualiza los clientes actuales.
- Mensajes de WhatsApp listos para enviar: recordatorio (uno solo si hay varias suscripciones con la misma fecha) y datos de la cuenta. Las plantillas se editan.
- Aviso diario de vencimientos sin servicios permanentes (WorkManager).
- Historial de renovaciones y cancelaciones, y resumen de ventas, costos y ganancia.
- Respaldo: exportar e importar todos los datos en un archivo.

## Pendiente antes de uso real.🐟

- Quitar `fallbackToDestructiveMigration()` en `data/AppDatabase.kt` (hoy, si cambia el esquema de la base de datos, se borran los datos).
- Compilar la versión `release` (más liviana) y revisar el consumo de batería.

## Privacidad

Los datos de clientes (incluidas contraseñas) nunca salen del celular. **No subas respaldos ni capturas con datos reales a GitHub** (el archivo de respaldo incluye contraseñas en texto plano).

## Licencia

[MIT](LICENSE)
