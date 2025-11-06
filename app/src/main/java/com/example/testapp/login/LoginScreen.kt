package com.example.testapp.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.testapp.MyApplication
import com.example.testapp.auth.UserData
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val database = (context.applicationContext as MyApplication).database
    val userDao = database.userDao()
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
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
/*TODO: Arreglar el fondo blanco y hacer que el texto de los cuadros de texto sean blancos y el fondo del mismo tema que el de la app*/