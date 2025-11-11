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
import androidx.navigation.NavController
import com.example.testapp.R
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienestarScreen(
    navController: NavController,
    onActionSelected: (String) -> Unit = {}
) {
    var mood by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bienestar",
                        color = PrimaryTextColor,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = PrimaryTextColor
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Acción bienestar",
                        tint = PrimaryTextColor,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackgroundColor.copy(alpha = 0.35f)
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
            ChipText("¿Cómo te encuentras hoy?")

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodIcon(
                    selected = mood == "feliz",
                    drawableResId = R.drawable.feliz
                ) { mood = "feliz" }

                MoodIcon(
                    selected = mood == "meh",
                    drawableResId = R.drawable.neh
                ) { mood = "meh" }

                MoodIcon(
                    selected = mood == "triste",
                    drawableResId = R.drawable.triste
                ) { mood = "triste" }
            }

            Spacer(Modifier.height(16.dp))

            ChipText("¿Por qué?")

            Spacer(Modifier.height(12.dp))

            // OJO: cada llamada incluye drawableResId y NO usa comillas “curvas”
            ActionCard(
                title = "Respiracion Guiada",
                drawableResId = R.drawable.respiracion
            ) { onActionSelected("respiracion") }

            ActionCard(
                title = "No pantalla",
                drawableResId = R.drawable.nopantalla
            ) { onActionSelected("no_pantalla") }

            ActionCard(
                title = "Paseo/Estiramiento",
                drawableResId = R.drawable.paseo
            ) { onActionSelected("paseo") }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChipText(text: String) {
    Text(
        text = text,
        color = PrimaryTextColor,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackgroundColor.copy(alpha = 0.35f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    )
}

@Composable
private fun MoodIcon(
    selected: Boolean,
    drawableResId: Int,
    onClick: () -> Unit
) {
    val ring = if (selected) CardBackgroundColor else Color.Transparent
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.7f) // todas tendrán el mismo ancho relativo
            .height(150.dp) // altura fija para uniformidad
            .clip(RoundedCornerShape(22.dp))
            .background(CardBackgroundColor.copy(alpha = 0.8f))
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
                color = PrimaryTextColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
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