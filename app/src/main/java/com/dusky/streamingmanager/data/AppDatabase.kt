package com.dusky.streamingmanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * La base de datos de la app (un archivo SQLite privado: ninguna otra app puede leerlo).
 *
 * IMPORTANTE: si cambias una entidad (agregar o quitar columnas), sube [version].
 * Mientras desarrollamos, al subir la versión se borra todo y se crea de nuevo
 * (fallbackToDestructiveMigration). Antes de usar la app "en serio" hay que quitar esa línea.
 */
@Database(
    entities = [Proveedor::class, Servicio::class, Suscripcion::class, Movimiento::class],
    version = 1,
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
                    .fallbackToDestructiveMigration() // TODO: quitar antes de uso real
                    .build()
                    .also { instancia = it }
            }
    }
}
