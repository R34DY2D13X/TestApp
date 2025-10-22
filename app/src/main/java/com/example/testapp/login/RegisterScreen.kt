package com.example.testapp.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(color = DarkBackground, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crea tu Cuenta", style = MaterialTheme.typography.headlineLarge, color = PrimaryTextColor)
            Text("Únete a la comunidad HabiCut", style = MaterialTheme.typography.titleMedium, color = PrimaryTextColor.copy(alpha = 0.8f))
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
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* TODO: Implement Registration Logic */
                    navController.navigate("login") { popUpTo("login") { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Registrarme")
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¿Ya tienes cuenta? Inicia Sesión",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}
