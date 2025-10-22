package com.example.testapp.auth

// Enum para definir los roles posibles
enum class UserRole {
    ADMIN,
    USER,
    NONE // Rol inicial antes del login
}

// Objeto singleton para guardar el estado del usuario en toda la app
object UserData {
    var role: UserRole = UserRole.NONE
}
