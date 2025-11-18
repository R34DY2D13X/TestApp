package com.example.temporizador

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private enum class TimerState {
    IDLE, RUNNING, PAUSED
}

private fun formatTime(seconds: Long): String {
    val minutes = TimeUnit.SECONDS.toMinutes(seconds)
    val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private val motivationalPhrases = listOf(
    "¡Hora de concentrarse!",
    "¡Vamos, tú puedes!",
    "¡Hazlo por tu yo del futuro!",
    "¡Menos procrastinación, más tareas!"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerUI(onBack: () -> Unit = {}) {
    val randomPhrase = remember { motivationalPhrases.random() }
    var isStudyTimeCompleted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enfoque", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = { IconButton({}) { Icon(Icons.Filled.Psychology, "Concentración", modifier = Modifier.size(36.dp)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB0C4DE), titleContentColor = Color.White, navigationIconContentColor = Color.White, actionIconContentColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFF252440)
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(randomPhrase, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, color = Color.White)

            TimerModule(
                title = "Tiempo de estudio:",
                initialTimeInMinutes = 25, // <-- VALOR RESTAURADO
                onTimerFinished = { isStudyTimeCompleted = true },
                completionTitle = "¡Tiempo de estudio completado!",
                completionMessage = "¡Buen trabajo! Ahora toma un merecido descanso."
            )

            Box(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isStudyTimeCompleted
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar("¡Primero tienes que estudiar, no descansar!")
                    }
                }
            ) {
                TimerModule(
                    title = "Descanso:",
                    initialTimeInMinutes = 10, // <-- VALOR RESTAURADO
                    isEnabled = isStudyTimeCompleted, // Se habilita cuando el estudio termina
                    completionTitle = "¡Muy bien!",
                    completionMessage = "Ya terminaste de estudiar por hoy, puedes continuar con otros módulos o intentar estudiar de nuevo más tarde.",
                    onCompletionAcknowledged = onBack // Vuelve al menú al terminar
                )
            }
        }
    }
}

@Composable
fun TimerModule(
    title: String,
    initialTimeInMinutes: Long,
    initialTimeInSecondsOverride: Long? = null, // Se mantiene por si se necesita para futuras pruebas
    isEnabled: Boolean = true,
    onTimerFinished: () -> Unit = {},
    completionTitle: String,
    completionMessage: String,
    onCompletionAcknowledged: () -> Unit = {}
) {
    val initialTimeInSeconds = initialTimeInSecondsOverride ?: TimeUnit.MINUTES.toSeconds(initialTimeInMinutes)
    var timeLeftInSeconds by remember { mutableStateOf(initialTimeInSeconds) }
    var timerState by remember { mutableStateOf(TimerState.IDLE) }
    val timer = remember { mutableStateOf<CountDownTimer?>(null) }
    val context = LocalContext.current
    val ringtone = remember { mutableStateOf<Ringtone?>(null) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    DisposableEffect(initialTimeInMinutes, isEnabled) {
        if (!isEnabled) { // Si se deshabilita, resetea el timer
            timer.value?.cancel()
            ringtone.value?.stop()
            timerState = TimerState.IDLE
            timeLeftInSeconds = initialTimeInSeconds
        }
        onDispose {
            timer.value?.cancel()
            ringtone.value?.stop()
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = {
                showCompletionDialog = false
                ringtone.value?.stop()
                onCompletionAcknowledged()
            },
            title = { Text(completionTitle, style = MaterialTheme.typography.headlineMedium) },
            text = { Text(completionMessage, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                Button(onClick = {
                    showCompletionDialog = false
                    ringtone.value?.stop()
                    onCompletionAcknowledged()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    val contentAlpha = if (isEnabled) 1f else 0.5f
    val activeColor = Color(0xFF64B5F6)
    val buttonColor = if (isEnabled) activeColor else Color.Gray.copy(alpha = 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(contentAlpha)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { timeLeftInSeconds.toFloat() / initialTimeInSeconds.toFloat() },
                modifier = Modifier.size(180.dp),
                color = buttonColor,
                strokeWidth = 8.dp,
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
            Text(
                text = formatTime(timeLeftInSeconds),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            val isPlayEnabled = timeLeftInSeconds > 0 && isEnabled

            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(buttonColor),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    when (timerState) {
                        TimerState.IDLE, TimerState.PAUSED -> {
                            timerState = TimerState.RUNNING
                            timer.value = object : CountDownTimer(timeLeftInSeconds * 1000, 1000) {
                                override fun onTick(millisUntilFinished: Long) { timeLeftInSeconds = millisUntilFinished / 1000 }
                                override fun onFinish() {
                                    timeLeftInSeconds = 0
                                    timerState = TimerState.IDLE
                                    onTimerFinished()
                                    try {
                                        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                                        ringtone.value = RingtoneManager.getRingtone(context, notificationUri)
                                        ringtone.value?.play()
                                        showCompletionDialog = true
                                    } catch (e: Exception) { e.printStackTrace() }
                                }
                            }.start()
                        }
                        TimerState.RUNNING -> {
                            timerState = TimerState.PAUSED
                            timer.value?.cancel()
                        }
                    }
                }, enabled = isPlayEnabled) {
                    Icon(if (timerState == TimerState.RUNNING) Icons.Filled.Pause else Icons.Filled.PlayArrow, "", modifier = Modifier.size(36.dp), tint = Color.White)
                }
            }
            
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape).background(buttonColor),
                contentAlignment = Alignment.Center
            ) {
                 IconButton(onClick = {
                    timer.value?.cancel()
                    ringtone.value?.stop()
                    timerState = TimerState.IDLE
                    timeLeftInSeconds = initialTimeInSeconds
                }, enabled = isEnabled) {
                    Icon(Icons.Filled.Replay, "Reiniciar", modifier = Modifier.size(36.dp), tint = Color.White)
                }
            }
        }
    }
}
