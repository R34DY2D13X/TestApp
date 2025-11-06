package com.example.testapp.splash


import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.testapp.R
import com.example.testapp.menu.MenuScreen
import com.example.testapp.ui.theme.DarkBackground
import com.example.testapp.ui.theme.PrimaryTextColor
import com.example.testapp.ui.theme.TestAppTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // This effect will run once when the composable is first displayed
    LaunchedEffect(key1 = true) {
        delay(2000) // Wait for 2 seconds
        navController.navigate("login") {
            // Remove SplashScreen from the back stack
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.mainlogo),
                contentDescription = "HabiCut Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = " ¡Bienvenid@s a HabiCut!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryTextColor
            )
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreen() {
    TestAppTheme {
        SplashScreen(navController = rememberNavController())
    }
}

