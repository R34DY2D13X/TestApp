package com.example.testapp.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor

// Data for Menu Cards
data class MenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector, // Using ImageVector for now
    val route: String
)

private val menuItems = listOf(
    MenuItem("Sueño", "¿Te desvelas mucho?", Icons.Default.Nightlight, "sueño"),
    MenuItem("Bienestar", "Cuida tu salud", Icons.Default.Favorite, "bienestar"),
    MenuItem("Temporizador", "Gestiona tu tiempo", Icons.Default.Timer, "temporizador"),
    MenuItem("Plan de Estudios", "Organiza tu aprendizaje", Icons.Default.Book, "plan_de_estudios")
)

// Data for Bottom Navigation
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Inicio", Icons.Default.Home, "menu"),
    BottomNavItem("Calendario", Icons.Default.CalendarMonth, "calendario"),
    BottomNavItem("Ajustes", Icons.Default.Settings, "ajustes")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {
    var currentRoute by rememberSaveable { mutableStateOf("menu") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            NavigationBar(containerColor = CardBackgroundColor.copy(alpha = 0.8f)) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            currentRoute = item.route
                            // This will navigate to other screens from the bottom bar if needed
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label, tint = PrimaryTextColor) },
                        label = { Text(item.label, color = PrimaryTextColor) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTextColor,
                            unselectedIconColor = PrimaryTextColor.copy(alpha = 0.6f),
                            selectedTextColor = PrimaryTextColor,
                            unselectedTextColor = PrimaryTextColor.copy(alpha = 0.6f),
                            indicatorColor = CardBackgroundColor
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(DarkBackground),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) { // Span the full width
                Text(
                    text = "MEJORA TUS HÁBITOS CON NOSOTROS",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PrimaryTextColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp) // Space between title and grid
                )
            }
            items(menuItems) { item ->
                MenuCard(item = item) {
                    navController.navigate(item.route)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.aspectRatio(0.8f),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
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
                tint = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title.uppercase(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = PrimaryTextColor.copy(alpha = 0.8f),
                lineHeight = 14.sp
            )
        }
    }
}
