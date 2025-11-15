package com.example.testapp.bienestar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.delay

@Composable
fun RespiracionScreen(navController: NavController) {

    var estado by remember { mutableStateOf("Inhala") }
    var comenzar by remember { mutableStateOf(false) }

    // Valor animado para el círculo
    val escalaObjetivo =
        when (estado) {
            "Inhala" -> 1.3f
            "Sostén" -> 1.3f
            "Exhala" -> 0.7f
            else -> 1f
        }

    val escala by animateFloatAsState(
        targetValue = escalaObjetivo,
        animationSpec = tween(durationMillis = 3000),
        label = "animacionRespiracion"
    )

    // Control del ciclo
    LaunchedEffect(comenzar) {
        if (comenzar) {
            while (comenzar) {
                estado = "Inhala"
                delay(4000)

                estado = "Sostén"
                delay(2000)

                estado = "Exhala"
                delay(4000)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Ejercicio de respiración guiada",
                color = PrimaryTextColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Círculo animado
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(escala)
                    .background(CardBackgroundColor.copy(alpha = 0.8f), CircleShape)
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = estado,
                color = PrimaryTextColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { comenzar = true },
                colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundColor),
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp)
            ) {
                Text("Iniciar", color = PrimaryTextColor, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { comenzar = false },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD46F6F)),
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp)
            ) {
                Text("Detener", color = Color.White, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB78B6A)),
                modifier = Modifier
                    .width(160.dp)
                    .height(50.dp)
            ) {
                Text("Regresar", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
