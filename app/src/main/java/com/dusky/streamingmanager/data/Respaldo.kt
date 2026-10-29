package com.dusky.streamingmanager.data

import androidx.room.withTransaction
import org.json.JSONArray
import org.json.JSONObject

/**
 * Exportar e importar todos los datos como un archivo de texto (JSON).
 * OJO: el archivo incluye correos y contraseñas de tus clientes en texto plano.
 * Guárdalo en un lugar privado y nunca lo subas a GitHub.
 * Las imágenes de los iconos no se incluyen (se vuelven a elegir).
 */
object Respaldo {

    private const val MARCA = "dusky-respaldo"
    private const val VERSION = 1

    suspend fun exportar(bd: AppDatabase): String {
        val proveedores = bd.proveedorDao().todos()
        val servicios = bd.servicioDao().todos()
        val suscripciones = bd.suscripcionDao().todas()
        val movimientos = bd.movimientoDao().todos()

        val raiz = JSONObject()
        raiz.put("marca", MARCA)
        raiz.put("version", VERSION)
        raiz.put("proveedores", JSONArray().also { a -> proveedores.forEach { a.put(proveedorAJson(it)) } })
        raiz.put("servicios", JSONArray().also { a -> servicios.forEach { a.put(servicioAJson(it)) } })
        raiz.put("suscripciones", JSONArray().also { a -> suscripciones.forEach { a.put(suscripcionAJson(it)) } })
        raiz.put("movimientos", JSONArray().also { a -> movimientos.forEach { a.put(movimientoAJson(it)) } })
        return raiz.toString(2)
    }

    /** Reemplaza TODOS los datos actuales por los del archivo. Lanza error si el archivo no es válido. */
    suspend fun importar(bd: AppDatabase, texto: String) {
        // Primero se lee todo; así, si el archivo está dañado, no se borra nada.
        val raiz = JSONObject(texto)
        require(raiz.optString("marca") == MARCA) { "Este archivo no es un respaldo de Dusky" }
        val proveedores = raiz.getJSONArray("proveedores").mapear { jsonAProveedor(it) }
        val servicios = raiz.getJSONArray("servicios").mapear { jsonAServicio(it) }
        val suscripciones = raiz.getJSONArray("suscripciones").mapear { jsonASuscripcion(it) }
        val movimientos = raiz.getJSONArray("movimientos").mapear { jsonAMovimiento(it) }

        bd.withTransaction {
            bd.movimientoDao().borrarTodo()
            bd.suscripcionDao().borrarTodo()
            bd.servicioDao().borrarTodo()
            bd.proveedorDao().borrarTodo()
            bd.proveedorDao().insertarTodos(proveedores)
            bd.servicioDao().insertarTodos(servicios)
            bd.suscripcionDao().insertarTodas(suscripciones)
            bd.movimientoDao().insertarTodos(movimientos)
        }
    }

    // ---------- Conversión entidad <-> JSON ----------

    private fun <T> JSONArray.mapear(f: (JSONObject) -> T): List<T> =
        List(length()) { f(getJSONObject(it)) }

    private fun JSONObject.texto(clave: String): String? =
        if (has(clave) && !isNull(clave)) getString(clave) else null

    private fun proveedorAJson(p: Proveedor) = JSONObject().apply {
        put("id", p.id)
        put("nombre", p.nombre)
        put("color", p.color)
        put("whatsappVentas", p.whatsappVentas)
        put("whatsappSoporte", p.whatsappSoporte)
    }

    private fun jsonAProveedor(o: JSONObject) = Proveedor(
        id = o.getLong("id"),
        nombre = o.getString("nombre"),
        color = o.getInt("color"),
        icono = null,
        whatsappVentas = o.texto("whatsappVentas"),
        whatsappSoporte = o.texto("whatsappSoporte"),
    )

    private fun servicioAJson(s: Servicio) = JSONObject().apply {
        put("id", s.id)
        put("proveedorId", s.proveedorId)
        put("nombre", s.nombre)
        put("esPerfil", s.esPerfil)
        put("costo", s.costo)
        put("precio", s.precio)
        put("color", s.color)
        put("diasPorDefecto", s.diasPorDefecto)
    }

    private fun jsonAServicio(o: JSONObject) = Servicio(
        id = o.getLong("id"),
        proveedorId = o.getLong("proveedorId"),
        nombre = o.getString("nombre"),
        esPerfil = o.getBoolean("esPerfil"),
        costo = o.getDouble("costo"),
        precio = o.getDouble("precio"),
        color = o.getInt("color"),
        icono = null,
        diasPorDefecto = o.getInt("diasPorDefecto"),
    )

    private fun suscripcionAJson(s: Suscripcion) = JSONObject().apply {
        put("id", s.id)
        put("nombreCliente", s.nombreCliente)
        put("telefono", s.telefono)
        put("usuarioWhatsApp", s.usuarioWhatsApp)
        put("servicioId", s.servicioId)
        put("proveedorId", s.proveedorId)
        put("servicioNombre", s.servicioNombre)
        put("servicioColor", s.servicioColor)
        put("costo", s.costo)
        put("precio", s.precio)
        put("fechaInicio", s.fechaInicio)
        put("fechaVencimiento", s.fechaVencimiento)
        put("correo", s.correo)
        put("contrasena", s.contrasena)
        put("perfil", s.perfil)
        put("pin", s.pin)
        put("nota", s.nota)
        put("activa", s.activa)
    }

    private fun jsonASuscripcion(o: JSONObject) = Suscripcion(
        id = o.getLong("id"),
        nombreCliente = o.getString("nombreCliente"),
        telefono = o.getString("telefono"),
        usuarioWhatsApp = o.texto("usuarioWhatsApp"),
        servicioId = if (o.has("servicioId") && !o.isNull("servicioId")) o.getLong("servicioId") else null,
        proveedorId = if (o.has("proveedorId") && !o.isNull("proveedorId")) o.getLong("proveedorId") else null,
        servicioNombre = o.getString("servicioNombre"),
        servicioColor = o.getInt("servicioColor"),
        costo = o.getDouble("costo"),
        precio = o.getDouble("precio"),
        fechaInicio = o.getLong("fechaInicio"),
        fechaVencimiento = o.getLong("fechaVencimiento"),
        correo = o.optString("correo", ""),
        contrasena = o.optString("contrasena", ""),
        perfil = o.texto("perfil"),
        pin = o.texto("pin"),
        nota = o.texto("nota"),
        activa = o.optBoolean("activa", true),
    )

    private fun movimientoAJson(m: Movimiento) = JSONObject().apply {
        put("id", m.id)
        put("tipo", m.tipo)
        put("nombreCliente", m.nombreCliente)
        put("servicioNombre", m.servicioNombre)
        put("costo", m.costo)
        put("precio", m.precio)
        put("fechaInicio", m.fechaInicio)
        put("fechaFin", m.fechaFin)
        put("registradoEn", m.registradoEn)
    }

    private fun jsonAMovimiento(o: JSONObject) = Movimiento(
        id = o.getLong("id"),
        tipo = o.getString("tipo"),
        nombreCliente = o.getString("nombreCliente"),
        servicioNombre = o.getString("servicioNombre"),
        costo = o.getDouble("costo"),
        precio = o.getDouble("precio"),
        fechaInicio = o.getLong("fechaInicio"),
        fechaFin = o.getLong("fechaFin"),
        registradoEn = o.getLong("registradoEn"),
    )
}
