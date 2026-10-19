
# Dusky Streaming Manager🔥

App de Android, liviana y de código abierto, para gestionar clientes con mensualidades de servicios de streaming:
vencimientos, ganancias, recordatorios por WhatsApp y notificaciones. Tema oscuro, sin internet, sin cuentas:
todos los datos se guardan solo en tu celular.

> **Estado:** v1.0.0

## Hecho con ayuda de IA🥀

El código de este proyecto fue escrito con ayuda de **Claude** (IA de Anthropic).
El diseño, las ideas y la dirección del proyecto son de **dusky_os**. Se comparte con licencia MIT para que
cualquiera pueda usarlo, estudiarlo y mejorarlo.

## Descargar e instalar👀

1. Entra a **Releases** de este repositorio.
2. Descarga `Dusky.apk` de la última versión (por ejemplo `v1.0.0`), o de **Última compilación** para la más reciente.
3. Ábrelo e instálalo (Android pedirá permitir "instalar apps de esta fuente").

Las compilaciones las hace GitHub Actions automáticamente (pestaña **Actions**, tarda unos minutos).
Cada versión nueva se instala encima de la anterior sin perder datos.

> El APK está firmado con la llave del proyecto, que vive solo en los *Secrets* del repositorio.
> Si lo compilas tú (fork), sin esa llave se firma con la llave de pruebas pública `app/debug.keystore`:
> funciona, pero no se puede instalar encima de la versión oficial.

## Publicar una versión nueva🍤

1. Sube `versionCode` (+1) y `versionName` en `app/build.gradle.kts`.
2. En GitHub: **Releases → Draft a new release**, crea la etiqueta (ej: `v1.1.0`) y publica.
3. Actions compila, firma y adjunta `Dusky.apk` a ese Release.

## Tecnología🔧

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

## Funciones🦝

- Lista de suscripciones ordenada por vencimiento, con barra de color por servicio (izquierda) y por días restantes (derecha: 0–5 rojo, 6–10 amarillo, 11+ verde).
- Tarjeta expandible con teléfono, costo, venta, ganancia, fechas, correo, contraseña, perfil, PIN y nota.
- Agregar, editar, renovar (queda en el historial), cancelar y eliminar (mantener pulsado, con "Deshacer").
- Proveedores (icono, color, WhatsApp de ventas y soporte) y servicios con costo y precio; al cambiar precios pregunta si actualiza los clientes actuales.
- Mensajes de WhatsApp listos para enviar: recordatorio (uno solo si hay varias suscripciones con la misma fecha) y datos de la cuenta. Las plantillas se editan.
- Aviso diario de vencimientos sin servicios permanentes (WorkManager).
- Historial de renovaciones y cancelaciones, y resumen de ventas, costos y ganancia.
- Respaldo: exportar e importar todos los datos en un archivo.

## Si cambias la base de datos⚠️

Si agregas o quitas columnas, sube la versión en `data/AppDatabase.kt` y escribe una `Migration`
(hay un ejemplo en el comentario de ese archivo). Sin migración la app se cierra a propósito,
para no borrar nunca los datos de tus clientes.

## Privacidad📄

Los datos de clientes (incluidas contraseñas) nunca salen del celular. **No subas respaldos ni capturas con datos reales a GitHub** (el archivo de respaldo incluye contraseñas en texto plano).

## Licencia

[MIT](LICENSE)
