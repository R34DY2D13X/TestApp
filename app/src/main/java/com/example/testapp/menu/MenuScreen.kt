package com.example.testapp.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

data class MenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

private val menuItems = listOf(
    MenuItem("Sueño", "¿Te desvelas mucho?", Icons.Default.Nightlight, "sueño"),
    MenuItem("Bienestar", "Cuida tu salud", Icons.Default.Favorite, "bienestar"),
    MenuItem("Temporizador", "Gestiona tu tiempo", Icons.Default.Timer, "temporizadorP"),
    MenuItem("Plan de Estudios", "Organiza tu aprendizaje", Icons.Default.Book, "plan_de_estudios")
)

@Composable
fun MenuScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    val dynamicBg = if (highContrast) Color.Black else DarkBackground
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().background(dynamicBg),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) { 
            Text(
                text = "MEJORA TUS HÁBITOS CON NOSOTROS",
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize),
                color = dynamicPrimaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        items(menuItems) { item ->
            MenuCard(item = item, settings = settings) {
                navController.navigate(item.route)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(item: MenuItem, settings: com.example.testapp.ajustes.SettingsState, onClick: () -> Unit) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    val dynamicCardBg = if (highContrast) Color(0xFF1C1C1E) else CardBackgroundColor
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else PrimaryTextColor.copy(alpha = 0.8f)

    Card(
        modifier = Modifier.aspectRatio(0.8f),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = dynamicCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp),
                tint = dynamicPrimaryText
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSize),
                fontWeight = FontWeight.Bold,
                color = dynamicPrimaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * fontSize),
                color = dynamicSecondaryText,
                lineHeight = (14.sp * fontSize)
            )
        }
    }
}