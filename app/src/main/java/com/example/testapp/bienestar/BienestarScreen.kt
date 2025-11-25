package com.example.testapp.bienestar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.R
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienestarScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel(),
    onActionSelected: (String) -> Unit = {}
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    var mood by remember { mutableStateOf<String?>(null) }

    val dynamicBg = if (highContrast) Color.Black else DarkBackground
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else CardBackgroundColor.copy(alpha = 0.8f)
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    Scaffold(
        containerColor = dynamicBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bienestar",
                        color = dynamicPrimaryText,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize * fontSize)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = dynamicPrimaryText
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Acción bienestar",
                        tint = dynamicPrimaryText,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (highContrast) Color(0xFF1A1A1A) else CardBackgroundColor.copy(alpha = 0.35f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChipText("¿Cómo te encuentras hoy?", fontSize, dynamicPrimaryText, dynamicCardBg)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodIcon(
                    selected = mood == "feliz",
                    drawableResId = R.drawable.feliz,
                    highContrast = highContrast
                ) { mood = "feliz" }

                MoodIcon(
                    selected = mood == "meh",
                    drawableResId = R.drawable.neh,
                    highContrast = highContrast
                ) { mood = "meh" }

                MoodIcon(
                    selected = mood == "triste",
                    drawableResId = R.drawable.triste,
                    highContrast = highContrast
                ) { mood = "triste" }
            }

            Spacer(Modifier.height(16.dp))

            ChipText("¿Por qué?", fontSize, dynamicPrimaryText, dynamicCardBg)

            Spacer(Modifier.height(12.dp))

            ActionCard(
                title = "Respiracion Guiada",
                drawableResId = R.drawable.respiracion,
                settings = settings
            ) { navController.navigate("respiracion") }

            ActionCard(
                title = "No pantalla",
                drawableResId = R.drawable.nopantalla,
                settings = settings
            ) { navController.navigate("no_pantalla") }

            ActionCard(
                title = "Paseo/Estiramiento",
                drawableResId =  R.drawable.paseo,
                settings = settings
            ) { navController.navigate("estiramiento") }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChipText(text: String, fontSize: Float, textColor: Color, cardBg: Color) {
    Text(
        text = text,
        color = textColor,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(cardBg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSize)
    )
}

@Composable
private fun MoodIcon(
    selected: Boolean,
    drawableResId: Int,
    highContrast: Boolean,
    onClick: () -> Unit
) {
    val ring = if (selected) (if (highContrast) Color.Yellow.copy(alpha = 0.8f) else CardBackgroundColor) else Color.Transparent
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(100))
            .background(ring.copy(alpha = if (selected) 0.35f else 0f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
    }
}

@Composable
private fun ActionCard(
    title: String,
    drawableResId: Int,
    settings: com.example.testapp.ajustes.SettingsState,
    onClick: () -> Unit
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else CardBackgroundColor.copy(alpha = 0.8f)
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.45f)
            .height(150.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(dynamicCardBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp)
        ) {
            Text(
                text = title,
                color = dynamicPrimaryText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp * fontSize,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize * fontSize
                )
            )
            Spacer(Modifier.height(10.dp))
            Image(
                painter = painterResource(id = drawableResId),
                contentDescription = null,
                modifier = Modifier.size(78.dp)
            )
        }
    }
}