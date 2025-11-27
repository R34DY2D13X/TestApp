package com.example.testapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.temporizador.TimerUI
import com.example.testapp.ajustes.AjustesScreen
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.bienestar.BienestarScreen
import com.example.testapp.bienestar.EstiramientoScreen
import com.example.testapp.bienestar.NoPantallaScreen
import com.example.testapp.bienestar.RespiracionScreen
import com.example.testapp.calendario.CalendarioScreen
import com.example.testapp.login.LoginScreen
import com.example.testapp.login.RegisterScreen
import com.example.testapp.menu.MenuScreen
import com.example.testapp.navigation.BottomNavigationBar
import com.example.testapp.plan_de_estudios.PlanDeEstudiosScreen
import com.example.testapp.splash.SplashScreen
import com.example.testapp.sueño.PlanDetalleScreen
import com.example.testapp.sueño.SueñoScreen
import com.example.testapp.ui.theme.TestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val settingsViewModel: SettingsViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    val activity = (context as? Activity)

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("menu", "calendario", "ajustes")
    
    var backPressedTime by remember { mutableStateOf(0L) }

    BackHandler(enabled = true) {
        if (currentRoute == "menu") {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                activity?.finish()
            } else {
                Toast.makeText(context, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show()
            }
            backPressedTime = System.currentTimeMillis()
        } else {
            navController.navigate("menu") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController, settingsViewModel = settingsViewModel)
            }
        }
    ) { innerPadding ->
        val settings by settingsViewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController, settingsViewModel) }
            composable("login") { LoginScreen(navController, settingsViewModel) }
            composable("register") { RegisterScreen(navController, settingsViewModel) }
            composable("menu") { MenuScreen(navController, settingsViewModel) }
            composable("home") { HomeScreen(settingsViewModel) }
            composable("sueño") { SueñoScreen(navController, settingsViewModel) }
            composable("bienestar") { BienestarScreen(navController, settingsViewModel) }
            composable("respiracion") { RespiracionScreen(navController, settingsViewModel) }
            composable("no_pantalla") { NoPantallaScreen(navController, settingsViewModel) }
            composable("estiramiento") { EstiramientoScreen(navController, settingsViewModel) }

            composable("temporizadorP") {
                TimerUI(
                    onBack = { navController.popBackStack() },
                    fontSize = settings.fontSize,
                    highContrast = settings.highContrast
                )
            }
            composable("plan_de_estudios") { PlanDeEstudiosScreen(navController, settingsViewModel) }
            composable("calendario") { CalendarioScreen(navController, settingsViewModel) }
            composable("ajustes") { AjustesScreen(navController, settingsViewModel) }

            composable(
                route = "plan_detalle/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.StringType })
            ) { backStackEntry ->
                PlanDetalleScreen(
                    navController = navController,
                    planId = backStackEntry.arguments?.getString("planId")
                )
            }
        }
    }
}