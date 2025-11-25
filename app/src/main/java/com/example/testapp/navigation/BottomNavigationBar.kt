package com.example.testapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.CardBackgroundColor
import com.example.testapp.ui.theme.PrimaryTextColor

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

@Composable
fun BottomNavigationBar(navController: NavController, settingsViewModel: SettingsViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val settings by settingsViewModel.uiState.collectAsState()

    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    val dynamicCardBg = if (highContrast) Color(0xFF1C1C1E) else CardBackgroundColor
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else PrimaryTextColor.copy(alpha = 0.6f)
    val accentColor = if (highContrast) Color.Yellow else PrimaryTextColor

    NavigationBar(
        containerColor = dynamicCardBg.copy(alpha = 0.9f)
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label, tint = if (currentRoute == item.route) accentColor else dynamicSecondaryText) },
                label = { Text(item.label, color = if (currentRoute == item.route) accentColor else dynamicSecondaryText, fontSize = 12.sp * fontSize) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}