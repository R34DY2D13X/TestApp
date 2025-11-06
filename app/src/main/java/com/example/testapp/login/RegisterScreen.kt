package com.example.testapp.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.MyApplication
import com.example.testapp.auth.UserRole
import com.example.testapp.data.db.User
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val database = (context.applicationContext as MyApplication).database
    val userDao = database.userDao()
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(color = DarkBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Crea tu Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryTextColor
            )
            Text(
                "Únete a la comunidad HabiCut",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryTextColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = PrimaryTextColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = PrimaryTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = PrimaryTextColor.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = PrimaryTextColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = PrimaryTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = PrimaryTextColor.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = PrimaryTextColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = PrimaryTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = PrimaryTextColor.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = PrimaryTextColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = PrimaryTextColor.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = PrimaryTextColor.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }
                coroutineScope.launch {
                    // Check if user already exists
                    val existingUser = userDao.getUserByEmail(email)
                    if (existingUser != null) {
                        errorMessage = "User with this email already exists"
                    } else {
                        // In a real app, hash the password!
                        val newUser = User(email = email, password = password, role = UserRole.USER)
                        userDao.insertUser(newUser)
                        // Navigate to login screen after successful registration
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }) {
                Text("Registrarse")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¿Ya tienes cuenta? Inicia Sesión",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}