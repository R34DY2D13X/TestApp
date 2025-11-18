package com.example.testapp

import android.app.Application
import androidx.room.Room
import com.example.testapp.data.db.AppDatabase

class MyApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "habicut-database"
        ).build()
    }
}
