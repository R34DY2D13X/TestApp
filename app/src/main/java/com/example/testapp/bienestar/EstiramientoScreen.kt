package com.example.testapp.bienestar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.PrimaryTextColor

@Composable
fun EstiramientoScreen(navController: NavController) {

    // Lista de estiramientos guiados
    val pasos = listOf(
        "Estiramiento de cuello" to "Inclina la cabeza a los lados suavemente.",
        "Estiramiento de hombros" to "Rota los hombros hacia adelante y atrás.",
        "Estiramiento de columna" to "Lleva tus manos al frente y arquea la espalda.",
        "Tocar puntas de los pies" to "Agáchate lentamente y estira las piernas."
    )

    var pasoActual by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(10) }
    var enCuentaAtras by remember { mutableStateOf(false) }

    LaunchedEffect(enCuentaAtras, pasoActual) {
        if (enCuentaAtras) {
            tiempo = 10
            while (tiempo > 0 && enCuentaAtras) {
                delay(1000)
                tiempo--
            }
            if (tiempo == 0 && pasoActual < pasos.size - 1) {
                pasoActual++
                tiempo = 10
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = pasos[pasoActual].first,
            color = PrimaryTextColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = pasos[pasoActual].second,
            color = Color(0xFFDEB28A),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = tiempo.toString(),
            color = PrimaryTextColor,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { enCuentaAtras = true },
            colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundColor),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Iniciar")
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                enCuentaAtras = false
                if (pasoActual < pasos.size - 1) pasoActual++ else navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundColor.copy(alpha = 0.8f)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Siguiente")
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEB28A)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Regresar")
        }
    }
}