package com.example.testapp.login

import android.widget.Toast
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.data.User
import com.example.testapp.data.UserRepository
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

@Composable
fun RegisterScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userRepository = UserRepository()

    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.Yellow else PrimaryTextColor.copy(alpha = 0.8f)
    val dynamicBorder = if (highContrast) Color.White else MaterialTheme.colorScheme.primary
    val dynamicUnfocusedBorder = if (highContrast) Color.Gray else PrimaryTextColor.copy(alpha = 0.5f)
    val dynamicLabel = if (highContrast) Color.Yellow else MaterialTheme.colorScheme.primary

    Surface(color = if (highContrast) Color.Black else DarkBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Crea tu Cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = MaterialTheme.typography.headlineLarge.fontSize * fontSize),
                color = dynamicPrimaryText
            )
            Text(
                "Únete a la comunidad HabiCut",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSize),
                color = dynamicSecondaryText
            )
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo", fontSize = 16.sp * fontSize) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp * fontSize),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = dynamicPrimaryText,
                    unfocusedTextColor = dynamicPrimaryText,
                    cursorColor = dynamicPrimaryText,
                    focusedBorderColor = dynamicBorder,
                    unfocusedBorderColor = dynamicUnfocusedBorder,
                    focusedLabelColor = dynamicLabel,
                    unfocusedLabelColor = dynamicSecondaryText
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico", fontSize = 16.sp * fontSize) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp * fontSize),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = dynamicPrimaryText,
                    unfocusedTextColor = dynamicPrimaryText,
                    cursorColor = dynamicPrimaryText,
                    focusedBorderColor = dynamicBorder,
                    unfocusedBorderColor = dynamicUnfocusedBorder,
                    focusedLabelColor = dynamicLabel,
                    unfocusedLabelColor = dynamicSecondaryText
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", fontSize = 16.sp * fontSize) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp * fontSize),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = dynamicPrimaryText,
                    unfocusedTextColor = dynamicPrimaryText,
                    cursorColor = dynamicPrimaryText,
                    focusedBorderColor = dynamicBorder,
                    unfocusedBorderColor = dynamicUnfocusedBorder,
                    focusedLabelColor = dynamicLabel,
                    unfocusedLabelColor = dynamicSecondaryText
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña", fontSize = 16.sp * fontSize) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp * fontSize),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = dynamicPrimaryText,
                    unfocusedTextColor = dynamicPrimaryText,
                    cursorColor = dynamicPrimaryText,
                    focusedBorderColor = dynamicBorder,
                    unfocusedBorderColor = dynamicUnfocusedBorder,
                    focusedLabelColor = dynamicLabel,
                    unfocusedLabelColor = dynamicSecondaryText
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp * fontSize)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(color = dynamicPrimaryText)
            } else {
                Button(onClick = {
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                        return@Button
                    }
                    if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Por favor, rellena todos los campos"
                        return@Button
                    }

                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val firebaseUser = auth.currentUser
                                val newUser = User(
                                    userId = firebaseUser?.uid ?: "",
                                    email = email,
                                    nombre = nombre
                                )
                                userRepository.insertUser(newUser)
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            } else {
                                errorMessage = when (task.exception) {
                                    is FirebaseAuthUserCollisionException -> "El correo ya está en uso."
                                    else -> "Error en el registro: ${task.exception?.message}"
                                }
                            }
                        }
                }) {
                    Text("Registrarse", fontSize = 16.sp * fontSize)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¿Ya tienes cuenta? Inicia Sesión",
                color = dynamicLabel,
                textAlign = TextAlign.Center,
                fontSize = 14.sp * fontSize,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}