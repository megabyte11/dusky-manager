package com.dusky.streamingmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * La base de datos de la app (un archivo SQLite privado: ninguna otra app puede leerlo).
 *
 * IMPORTANTE: si cambias una entidad (agregar o quitar columnas) hay que:
 *   1) subir [version],
 *   2) escribir una Migration en Migraciones.kt que conserve los datos y agregarla abajo
 *      con .addMigrations(...).
 * Sin migración la app se cierra al abrir (a propósito: nunca borra los datos de tus clientes).
 */
@Database(
    entities = [Proveedor::class, Servicio::class, Suscripcion::class, Movimiento::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun proveedorDao(): ProveedorDao
    abstract fun servicioDao(): ServicioDao
    abstract fun suscripcionDao(): SuscripcionDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        /** Devuelve siempre la misma base de datos (se crea la primera vez que se pide). */
        fun obtener(contexto: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "dusky.db",
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also { instancia = it }
            }
    }
}
