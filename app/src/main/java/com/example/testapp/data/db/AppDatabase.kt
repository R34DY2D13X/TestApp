package com.example.testapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// --- MIGRACIÓN MANUAL ---
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN nombre TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [User::class],
    version = 2,
    exportSchema = false // <-- Lo ponemos en false para evitar problemas con los esquemas
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
