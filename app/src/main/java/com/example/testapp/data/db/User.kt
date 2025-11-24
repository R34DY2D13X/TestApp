package com.example.testapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.testapp.auth.UserRole

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String, // In a real app, this should be a hash!
    val role: UserRole
)
