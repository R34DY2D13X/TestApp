package com.example.testapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapp.auth.UserRole

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val nombre: String, // <-- CAMPO AÑADIDO
    val password: String,
    val role: UserRole
)
