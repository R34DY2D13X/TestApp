package com.example.testapp.bienestar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import kotlinx.coroutines.delay

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoPantallaScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    val dynamicBg = if (highContrast) Color.Black else DarkBackground
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicAccentColor = if (highContrast) Color.Yellow else Color(0xFFF2B880)
    val dynamicButtonTextColor = if (highContrast) Color.Black else Color(0xFF252440)

    var contador by remember { mutableStateOf(300) } // 5 min en segundos
    var enEjecucion by remember { mutableStateOf(false) }

    LaunchedEffect(enEjecucion) {
        while (enEjecucion && contador > 0) {
            delay(1000)
            contador--
        }
    }

    Scaffold(
        containerColor = dynamicBg,
        topBar = {
            TopAppBar(
                title = { Text("Modo sin pantalla", color = dynamicPrimaryText, fontSize = 20.sp * fontSize) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = dynamicPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = dynamicBg)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(dynamicBg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Evita usar tu celular durante este tiempo",
                fontSize = 22.sp * fontSize,
                color = dynamicAccentColor,
                lineHeight = 28.sp * fontSize
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "${contador / 60}:${String.format("%02d", contador % 60)}",
                fontSize = 48.sp * fontSize,
                color = dynamicPrimaryText
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { enEjecucion = !enEjecucion },
                colors = ButtonDefaults.buttonColors(containerColor = dynamicAccentColor),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    if (enEjecucion) "Pausar" else "Iniciar",
                    color = dynamicButtonTextColor,
                    fontSize = 18.sp * fontSize
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = dynamicAccentColor),
                modifier = Modifier.width(160.dp)
            ) {
                Text(
                    "Regresar",
                    color = dynamicButtonTextColor,
                    fontSize = 18.sp * fontSize
                )
            }
        }
    }
}