package com.example.testapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import com.example.testapp.temporizador.TemporizadorScreen
import com.example.testapp.ui.theme.TestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("menu") {
            MenuScreen(navController = navController)
        }
        composable("plan_de_estudios") {
            PlanDeEstudiosScreen(navController = navController)
        }
        composable("sueño") {
            SueñoScreen(navController = navController)
        }
        composable("bienestar") {
            BienestarScreen(navController = navController)
        }
        composable("temporizador") {
            TemporizadorScreen(navController = navController)
        }
        composable("calendario") {
            CalendarioScreen(navController = navController)
        }
        composable("ajustes") {
            AjustesScreen(navController = navController)
        }
        composable(
            "plan_detalle/{planId}",
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            PlanDetalleScreen(
                navController = navController,
                planId = backStackEntry.arguments?.getString("planId")
            )
        }
    }
}