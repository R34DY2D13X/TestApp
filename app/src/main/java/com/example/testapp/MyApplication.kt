package com.example.testapp

import android.app.Application
import androidx.room.Room
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.MIGRATION_1_2

class MyApplication : Application() {
    val database: AppDatabase by lazy { 
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "habicut-db"
        )
        .addMigrations(MIGRATION_1_2) // <-- MIGRACIÓN AÑADIDA
        .build()
    }
}
