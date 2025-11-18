package com.example.testapp.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.ui.theme.ButtonPrimary
import com.example.testapp.ui.theme.GradientEnd
import com.example.testapp.ui.theme.LinkGreen
import com.example.testapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(navController: NavController) {

    // -----------------------------
    // ESTADOS
    // -----------------------------
    var fontSize by rememberSaveable { mutableStateOf(1f) }
    var highContrast by rememberSaveable { mutableStateOf(false) }
    var accessibilityMode by rememberSaveable { mutableStateOf(false) }

    var notifHabitos by rememberSaveable { mutableStateOf(true) }
    var notifSueno by rememberSaveable { mutableStateOf(true) }
    var notifEstudios by rememberSaveable { mutableStateOf(false) }

    var reminderTime by rememberSaveable { mutableStateOf("08:00 AM") }

    var showNameDialog by remember { mutableStateOf(false) }
    var userName by rememberSaveable { mutableStateOf("Usuario") }

    // -----------------------------
    // CONFIGURACIONES DINÁMICAS
    // -----------------------------

    val dynamicTextColor = if (highContrast) Color.White else Color.White.copy(alpha = 0.9f)
    val dynamicSecondary = if (highContrast) Color.Yellow else TextSecondary
    val cardBg = if (highContrast) Color(0xFF1A1A1A) else ButtonPrimary.copy(alpha = 0.35f)

    val baseFont = (16.sp * fontSize)
    val subtitleFont = (13.sp * fontSize)

    Scaffold(
        containerColor = GradientEnd,
        topBar = {
            TopAppBar(
                title = {
                    Text("Ajustes", color = Color.White, fontSize = 20.sp * fontSize)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // -----------------------------
            // PERFIL DEL USUARIO
            // -----------------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(LinkGreen)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 20.sp * fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Toque para cambiar nombre",
                        color = dynamicSecondary,
                        fontSize = 13.sp * fontSize,
                        modifier = Modifier.clickable { showNameDialog = true }
                    )
                }
            }

            // -----------------------------
            // PERSONALIZACIÓN
            // -----------------------------
            AjustesHeader("Personalización", dynamicSecondary, fontSize)

            AjustesCard(cardBg) {

                AjusteOpcion(
                    icon = Icons.Default.TextFields,
                    title = "Tamaño de letra",
                    subtitle = when {
                        fontSize < 1f -> "Pequeño"
                        fontSize == 1f -> "Normal"
                        else -> "Grande"
                    },
                    textColor = dynamicTextColor,
                    subtitleColor = dynamicSecondary,
                    fontSize = fontSize
                ) {}

                Slider(
                    value = fontSize,
                    onValueChange = { fontSize = it },
                    valueRange = 0.8f..1.6f,
                    colors = SliderDefaults.colors(
                        thumbColor = LinkGreen,
                        activeTrackColor = LinkGreen
                    )
                )

                AjusteSwitch(
                    icon = Icons.Default.Contrast,
                    title = "Alto contraste",
                    checked = highContrast,
                    textColor = dynamicTextColor,
                    fontSize = fontSize,
                    onCheckedChange = { highContrast = it }
                )

                AjusteSwitch(
                    icon = Icons.Default.AccessibilityNew,
                    title = "Modo accesible",
                    checked = accessibilityMode,
                    textColor = dynamicTextColor,
                    fontSize = fontSize,
                    onCheckedChange = { accessibilityMode = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -----------------------------
            // NOTIFICACIONES
            // -----------------------------
            AjustesHeader("Notificaciones", dynamicSecondary, fontSize)

            AjustesCard(cardBg) {
                AjusteSwitch(
                    icon = Icons.Default.CheckCircle,
                    title = "Hábitos diarios",
                    checked = notifHabitos,
                    textColor = dynamicTextColor,
                    fontSize = fontSize,
                    onCheckedChange = { notifHabitos = it }
                )

                AjusteSwitch(
                    icon = Icons.Default.NightsStay,
                    title = "Sueño",
                    checked = notifSueno,
                    textColor = dynamicTextColor,
                    fontSize = fontSize,
                    onCheckedChange = { notifSueno = it }
                )

                AjusteSwitch(
                    icon = Icons.Default.School,
                    title = "Plan de estudios",
                    checked = notifEstudios,
                    textColor = dynamicTextColor,
                    fontSize = fontSize,
                    onCheckedChange = { notifEstudios = it }
                )

                AjusteOpcion(
                    icon = Icons.Default.AccessTime,
                    title = "Horario de recordatorios",
                    subtitle = reminderTime,
                    textColor = dynamicTextColor,
                    subtitleColor = dynamicSecondary,
                    fontSize = fontSize
                ) {}
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -----------------------------
            // CUENTA
            // -----------------------------
            AjustesHeader("Cuenta", dynamicSecondary, fontSize)

            AjustesCard(cardBg) {
                AjusteOpcion(
                    icon = Icons.Default.Person,
                    title = "Cambiar nombre",
                    subtitle = userName,
                    textColor = dynamicTextColor,
                    subtitleColor = dynamicSecondary,
                    fontSize = fontSize
                ) { showNameDialog = true }

                AjusteOpcion(
                    icon = Icons.Default.Info,
                    title = "Versión",
                    subtitle = "1.0.0",
                    textColor = dynamicTextColor,
                    subtitleColor = dynamicSecondary,
                    fontSize = fontSize
                ) {}
            }
        }
    }

    if (showNameDialog) {
        DialogCambiarNombre(
            initial = userName,
            onDismiss = { showNameDialog = false },
            onSave = {
                userName = it
                showNameDialog = false
            }
        )
    }
}

/* ------------------------------
      COMPONENTES PERSONALIZADOS
------------------------------ */

@Composable
fun AjustesHeader(titulo: String, color: Color, scale: Float) {
    Text(
        text = titulo,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp * scale,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun AjustesCard(bg: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun AjusteOpcion(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color,
    subtitleColor: Color,
    fontSize: Float,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = LinkGreen)

        Spacer(modifier = Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(title, color = textColor, fontSize = 16.sp * fontSize)
            Text(subtitle, color = subtitleColor, fontSize = 13.sp * fontSize)
        }

        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = subtitleColor)
    }
}

@Composable
fun AjusteSwitch(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    textColor: Color,
    fontSize: Float,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = LinkGreen)
        Spacer(modifier = Modifier.width(12.dp))

        Text(
            title,
            modifier = Modifier.weight(1f),
            color = textColor,
            fontSize = 16.sp * fontSize
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LinkGreen,
                checkedTrackColor = LinkGreen.copy(alpha = 0.5f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogCambiarNombre(initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {

    var nombre by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onSave(nombre) },
                colors = ButtonDefaults.buttonColors(containerColor = LinkGreen)
            ) {
                Text("Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        title = { Text("Cambiar nombre", color = Color.White) },
        text = {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LinkGreen,
                    unfocusedBorderColor = TextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        },
        containerColor = ButtonPrimary.copy(alpha = 0.95f)
    )
}
