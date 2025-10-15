package com.example.temporizador

import android.os.CountDownTimer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerUI() {
    var timeLeft by remember { mutableStateOf(10L) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }


    DisposableEffect(Unit) {
        onDispose {
            timer?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$timeLeft segundos",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            timer?.cancel()
            timer = object : CountDownTimer(timeLeft * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeLeft = millisUntilFinished / 1000
                }

                override fun onFinish() {
                    timeLeft = 0
                }
            }
            timer?.start()
        }) {
            Text("Iniciar Temporizador")
        }
    }
}
