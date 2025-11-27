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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.delay

@Composable
fun EstiramientoScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    val dynamicBg = if (highContrast) Color.Black else DarkBackground
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicCardBg = if (highContrast) Color(0xFF1C1C1E) else CardBackgroundColor
    val dynamicAccentColor = if (highContrast) Color.Yellow else Color(0xFFDEB28A)

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
            .background(dynamicBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = pasos[pasoActual].first,
            color = dynamicPrimaryText,
            fontSize = 24.sp * fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = pasos[pasoActual].second,
            color = dynamicAccentColor,
            fontSize = 18.sp * fontSize,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = tiempo.toString(),
            color = dynamicPrimaryText,
            fontSize = 48.sp * fontSize,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { enCuentaAtras = true },
            colors = ButtonDefaults.buttonColors(containerColor = dynamicCardBg),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Iniciar", color = dynamicPrimaryText, fontSize = 16.sp * fontSize)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                enCuentaAtras = false
                if (pasoActual < pasos.size - 1) pasoActual++ else navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = dynamicCardBg.copy(alpha = 0.8f)),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Siguiente", color = dynamicPrimaryText, fontSize = 16.sp * fontSize)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = { navController.popBackStack() },
            colors = ButtonDefaults.buttonColors(containerColor = dynamicAccentColor),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Regresar", color = if (highContrast) Color.Black else Color.White, fontSize = 16.sp * fontSize)
        }
    }
}