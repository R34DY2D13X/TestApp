package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.temporizador.TimerUI
import com.example.testapp.ajustes.AjustesScreen
import com.example.testapp.bienestar.BienestarScreen
import com.example.testapp.bienestar.EstiramientoScreen
import com.example.testapp.bienestar.NoPantallaScreen
import com.example.testapp.bienestar.RespiracionScreen
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
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF252440)) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "splash") { // <-- RUTA INICIAL RESTAURADA
                        composable("splash") { SplashScreen(navController) } 
                        composable("login") { LoginScreen(navController) }
                        composable("register") { RegisterScreen(navController) }
                        composable("menu") { MenuScreen(navController) }
                        composable("sueño") { SueñoScreen(navController) }
                        composable("bienestar") { BienestarScreen(navController) }
                        composable("respiracion") { RespiracionScreen(navController) }
                        composable("no_pantalla") { NoPantallaScreen(navController) } 
                        composable("estiramiento") { EstiramientoScreen(navController) }

                        composable("temporizador") { 
                            TimerUI(onBack = { navController.popBackStack() })
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