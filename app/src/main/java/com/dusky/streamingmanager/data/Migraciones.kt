package com.dusky.streamingmanager.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/*
 * MIGRACIONES: cómo se actualiza la base de datos cuando cambia su estructura SIN perder datos.
 * Cada vez que se cambie una entidad: subir `version` en AppDatabase, escribir aquí una
 * Migration nueva y agregarla en AppDatabase con .addMigrations(...).
 */

/** Versión 1 -> 2: los clientes pueden tener un usuario de WhatsApp (@usuario) además del teléfono. */
val MIGRACION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE suscripciones ADD COLUMN usuarioWhatsApp TEXT")
    }
}
