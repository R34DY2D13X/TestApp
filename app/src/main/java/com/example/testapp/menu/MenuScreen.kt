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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.*
import androidx.navigation.compose.rememberNavController
import com.example.testapp.auth.UserData
import com.example.testapp.auth.UserRole
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import com.example.testapp.ui.theme.TestAppTheme

data class MenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

private val menuItems = listOf(
    MenuItem("Sueño", "¿Te desvelas mucho?", Icons.Default.Nightlight, "sueño"),
    MenuItem("Bienestar", "Cuida tu salud", Icons.Default.Favorite, "bienestar"),
    MenuItem("Temporizador", "Gestiona tu tiempo", Icons.Default.Timer, "temporizador"),
    MenuItem("Plan de Estudios", "Organiza tu aprendizaje", Icons.Default.Book, "plan_de_estudios")
)

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
        containerColor = GradientEnd, // Fondo principal oscuro
        bottomBar = {
            NavigationBar(
                containerColor = GradientEnd, // Mismo fondo oscuro para consistencia
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            currentRoute = item.route
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(28.dp)) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LinkGreen, // Color activo verde
                            selectedTextColor = LinkGreen,
                            unselectedIconColor = TextSecondary, // Color inactivo gris
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color.Transparent // Sin fondo para el ícono seleccionado
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(GradientEnd),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Mejora tus hábitos con nosotros",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
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
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(12.dp)), // Sombra ligera
        onClick = onClick,
        shape = RoundedCornerShape(12.dp), // Bordes redondeados
        colors = CardDefaults.cardColors(containerColor = ButtonPrimary) // Fondo de tarjeta azul
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title, // Texto alternativo
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}
