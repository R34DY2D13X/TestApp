package com.example.testapp.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.testapp.ui.theme.ButtonPrimary
import com.example.testapp.ui.theme.GradientEnd
import com.example.testapp.ui.theme.LinkGreen
import com.example.testapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {

    val settings by settingsViewModel.uiState.collectAsState()
    val currentUser by settingsViewModel.currentUser.collectAsState(initial = null)
    var showNameDialog by remember { mutableStateOf(false) }

    val dynamicTextColor = if (settings.highContrast) Color.White else Color.White.copy(alpha = 0.9f)
    val dynamicSecondary = if (settings.highContrast) Color.Yellow else TextSecondary
    val cardBg = if (settings.highContrast) Color(0xFF1A1A1A) else ButtonPrimary.copy(alpha = 0.35f)
    val fontSize = settings.fontSize
    val dynamicBg = if (settings.highContrast) Color.Black else GradientEnd

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

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
                    text = currentUser?.nombre ?: "Cargando...",
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
                fontSize = fontSize,
                onClick = {}
            )
            Slider(
                value = fontSize,
                onValueChange = { settingsViewModel.updateFontSize(it) },
                valueRange = 0.8f..1.6f,
                colors = SliderDefaults.colors(thumbColor = LinkGreen, activeTrackColor = LinkGreen)
            )
            AjusteSwitch(
                icon = Icons.Default.Contrast,
                title = "Alto contraste",
                checked = settings.highContrast,
                textColor = dynamicTextColor,
                fontSize = fontSize,
                onCheckedChange = { settingsViewModel.updateHighContrast(it) }
            )
            AjusteSwitch(
                icon = Icons.Default.AccessibilityNew,
                title = "Modo accesible",
                checked = settings.accessibilityMode,
                textColor = dynamicTextColor,
                fontSize = fontSize,
                onCheckedChange = { settingsViewModel.updateAccessibilityMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AjustesHeader("Notificaciones", dynamicSecondary, fontSize)
        AjustesCard(cardBg) {
            AjusteSwitch(
                icon = Icons.Default.CheckCircle,
                title = "Hábitos diarios",
                checked = settings.notifHabitos,
                textColor = dynamicTextColor,
                fontSize = fontSize,
                onCheckedChange = { settingsViewModel.updateNotifHabitos(it) }
            )
            AjusteSwitch(
                icon = Icons.Default.NightsStay,
                title = "Sueño",
                checked = settings.notifSueno,
                textColor = dynamicTextColor,
                fontSize = fontSize,
                onCheckedChange = { settingsViewModel.updateNotifSueno(it) }
            )
            AjusteSwitch(
                icon = Icons.Default.School,
                title = "Plan de estudios",
                checked = settings.notifEstudios,
                textColor = dynamicTextColor,
                fontSize = fontSize,
                onCheckedChange = { settingsViewModel.updateNotifEstudios(it) }
            )
            AjusteOpcion(
                icon = Icons.Default.AccessTime,
                title = "Horario de recordatorios",
                subtitle = settings.reminderTime,
                textColor = dynamicTextColor,
                subtitleColor = dynamicSecondary,
                fontSize = fontSize,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                settingsViewModel.updateLoginState(false)
                navController.navigate("login") { 
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }
    }

    if (showNameDialog) {
        var nombre by remember { mutableStateOf(currentUser?.nombre ?: "") }
        DialogCambiarNombre(
            nombre = nombre,
            onNombreChange = { nombre = it },
            onDismiss = { showNameDialog = false },
            onSave = {
                settingsViewModel.updateUserName(nombre)
                showNameDialog = false
            }
        )
    }
}

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

        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = subtitleColor)
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
fun DialogCambiarNombre(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onDismiss: () -> Unit, 
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onSave,
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
                onValueChange = onNombreChange,
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
