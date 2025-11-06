package com.example.testapp.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.MyApplication
import com.example.testapp.auth.UserData
import com.example.testapp.ui.theme.ButtonPrimary
import com.example.testapp.ui.theme.GradientEnd
import com.example.testapp.ui.theme.PrimaryTextColor
import com.example.testapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val database = (context.applicationContext as MyApplication).database
    val userDao = database.userDao()
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(color = GradientEnd, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido a HabiCut!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inicia sesión para continuar",
                color = TextSecondary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ButtonPrimary,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = ButtonPrimary,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = ButtonPrimary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description =
                        if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description, tint = Color.Gray)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ButtonPrimary,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = ButtonPrimary,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = PrimaryTextColor,
                    unfocusedTextColor = PrimaryTextColor,
                    cursorColor = ButtonPrimary
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(onClick = {
                coroutineScope.launch {
                    val user = userDao.getUserByEmail(email)
                    if (user == null) {
                        errorMessage = "Aun no tienes una cuenta, inicia tu registro"
                    } else if (user.password != password) { // In a real app, compare hashed passwords
                        errorMessage = "Contraseña incorrecta"
                    } else {
                        // Login successful, update UserData and navigate
                        UserData.role = user.role
                        navController.navigate("menu") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }) {
                Text("Iniciar sesion")
            }
            TextButton(onClick = { navController.navigate("Regsistrar") }) {
                Text("No tienes cuenta? registrate!")
            }
        }
    }
}