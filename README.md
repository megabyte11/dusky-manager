# Dusky Streaming Manager

App de Android, liviana y de código abierto, para gestionar clientes con mensualidades de servicios de streaming:
vencimientos, ganancias, recordatorios por WhatsApp y notificaciones. Tema oscuro, sin internet, sin cuentas:
todos los datos se guardan solo en tu celular.

> **Estado:** en desarrollo (v0.1.0 — Fase 1: base del proyecto).

## Hecho con ayuda de IA

El código de este proyecto fue escrito con ayuda de **Claude** (IA de Anthropic).
El diseño, las ideas y la dirección del proyecto son de **dusky_os**. Se comparte con licencia MIT para que
cualquiera pueda usarlo, estudiarlo y mejorarlo.

## Descargar e instalar

1. Entra a la pestaña **Releases** de este repositorio y abre **Última compilación**.
2. Descarga `app-debug.apk` desde tu celular.
3. Ábrelo e instálalo (Android pedirá permitir "instalar apps de esta fuente").

Cada vez que se suben cambios a la rama `main`, GitHub compila un APK nuevo automáticamente
(pestaña **Actions**, tarda unos minutos). Se puede instalar encima del anterior sin perder datos.

> El APK está firmado con una llave de pruebas pública (`app/debug.keystore`).
> Sirve para instalar y probar, **no** para publicar en Google Play.

## Tecnología

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

## Hoja de ruta

1. Base del proyecto, tema oscuro, base de datos, 3 pestañas
2. Clientes: tarjeta expandible, agregar / editar / renovar / eliminar
3. Ajustes: proveedores, servicios y actualización de costos
4. Mensajes de WhatsApp y notificaciones de vencimiento
5. Historial completo, resumen y respaldo (exportar / importar)
6. Versión final liviana (release)

## Privacidad

Los datos de clientes (incluidas contraseñas) nunca salen del celular. **No subas respaldos ni capturas con datos reales a GitHub.**

## Licencia

[MIT](LICENSE)
