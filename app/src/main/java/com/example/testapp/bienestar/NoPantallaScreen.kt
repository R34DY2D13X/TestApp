package com.example.testapp.bienestar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoPantallaScreen(navController: NavController) {

    var contador by remember { mutableStateOf(300) } // 5 min en segundos
    var enEjecucion by remember { mutableStateOf(false) }

    LaunchedEffect(enEjecucion) {
        while (enEjecucion && contador > 0) {
            delay(1000)
            contador--
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modo sin pantalla") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Evita usar tu celular durante este tiempo",
                fontSize = 22.sp,
                color = Color(0xFFF2B880),
                lineHeight = 28.sp // CORREGIDO: Se eliminó el punto extra.
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "${contador / 60}:${String.format("%02d", contador % 60)}",
                fontSize = 48.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { enEjecucion = !enEjecucion },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2B880)),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    if (enEjecucion) "Pausar" else "Iniciar",
                    color = Color(0xFF252440)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2B880)),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    "Regresar",
                    color = Color(0xFF252440)
                )
            }
        }
    }
}
