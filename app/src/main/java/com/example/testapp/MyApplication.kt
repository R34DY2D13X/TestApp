package com.example.testapp

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Habilitar la persistencia de datos offline de Firebase.
        // Esto debe hacerse antes de cualquier otra operación de base de datos.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
