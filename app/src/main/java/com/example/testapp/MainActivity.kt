package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.temporizador.TimerUI
import com.example.testapp.ajustes.AjustesScreen
import com.example.testapp.bienestar.BienestarScreen
import com.example.testapp.calendario.CalendarioScreen
import com.example.testapp.login.LoginScreen
import com.example.testapp.login.RegisterScreen
import com.example.testapp.menu.MenuScreen
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
                Surface(color = Color(0xFF252440)) {  // <- Aquí pones tu color de fondo
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") { // <- RUTA INICIAL RESTAURADA
                        composable("splash") { SplashScreen(navController) } // <- SPLASHSCREEN RESTAURADA
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) } // <- RUTA DE REGISTRO AÑADIDA
                        composable("menu") { MenuScreen(navController) }
                        composable("sueño") { SueñoScreen(navController) }
                        composable("bienestar") { BienestarScreen(navController) }
                        composable("temporizadorP") { 
                            TimerUI(onBack = { navController.popBackStack() }) // <- CAMBIO AQUÍ
                        }
                        composable("plan_de_estudios") { PlanDeEstudiosScreen(navController) }
                        composable("calendario") { CalendarioScreen(navController) }
                        composable("ajustes") { AjustesScreen(navController) }

                        // Nueva ruta para el detalle del plan
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
        }
    }
}
