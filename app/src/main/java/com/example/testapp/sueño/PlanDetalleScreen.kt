package com.example.testapp.sueño

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.testapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetalleScreen(navController: NavController, planId: String?) {
    val plan = remember { planId?.let { SueñoRepository.obtenerPlanPorId(it) } }

    var showInfoDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    val checkboxStates = remember { mutableStateMapOf<Int, Boolean>().apply {
        plan?.tareas?.forEach { put(it.id, false) }
    } }

    if (plan == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val completedTasks = checkboxStates.count { it.value }.toFloat()
    val totalTasks = plan.tareas.size.toFloat()
    val progress = if (totalTasks > 0) completedTasks / totalTasks else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "Progress Animation")

    Scaffold(
        containerColor = GradientEnd,
        topBar = { TopBar(navController, plan, onInfoClick = { showInfoDialog = true }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { ProgressHeader(animatedProgress, completedTasks.toInt(), totalTasks.toInt()) }
            item { InfoCard("Objetivo del Plan", plan.objetivo) }
            item { InfoCard("Duración Sugerida", plan.duracion) }
            item { TareasHeader() }
            items(plan.tareas, key = { it.id }) { tarea ->
                TareaItem(tarea, checkboxStates[tarea.id] ?: false) {
                    checkboxStates[tarea.id] = !checkboxStates.getOrDefault(tarea.id, false)
                    if (checkboxStates.all { entry -> entry.value }) {
                        showCompletionDialog = true
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        InfoDialog(info = plan.infoMedica, onDismiss = { showInfoDialog = false })
    }
    if (showCompletionDialog) {
        CompletionDialog(onDismiss = {
            showCompletionDialog = false
            navController.popBackStack()
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navController: NavController, plan: PlanDeSueño, onInfoClick: () -> Unit) {
    TopAppBar(
        title = { Text(plan.nombre, color = Color.White, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = Color.White) } },
        actions = { IconButton(onClick = onInfoClick) { Icon(Icons.Default.Info, "Información Médica", tint = Color.White) } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun ProgressHeader(progress: Float, completed: Int, total: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 24.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = ButtonPrimary.copy(alpha = 0.3f),
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = LinkGreen, // Color de acento para el progreso
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
        Text("Has completado $completed de $total tareas", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
    }
}

@Composable
private fun InfoCard(title: String, text: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = ButtonPrimary.copy(alpha = 0.2f))) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        }
    }
}

@Composable
private fun TareasHeader() {
    Text("Rutina Diaria", style = MaterialTheme.typography.headlineSmall, color = Color.White, modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp))
}

@Composable
private fun TareaItem(tarea: TareaDiaria, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isChecked) ButtonPrimary.copy(alpha = 0.3f) else ButtonPrimary.copy(alpha = 0.6f)),
        onClick = { onCheckedChange(!isChecked) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = LinkGreen, uncheckedColor = TextSecondary, checkmarkColor = GradientEnd)
            )
            Spacer(Modifier.size(16.dp))
            Text(tarea.descripcion, style = MaterialTheme.typography.bodyLarge, color = if(isChecked) TextSecondary else Color.White)
        }
    }
}

@Composable
private fun InfoDialog(info: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = GradientEnd, contentColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Información Clave", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(info, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp, color = TextSecondary)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End), colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)) {
                    Text("Entendido", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CompletionDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = GradientEnd, contentColor = Color.White)) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎉", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text("¡Felicidades!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Has completado todas las tareas de este plan. ¡Sigue así!", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary)) {
                    Text("Continuar", color = Color.White)
                }
            }
        }
    }
}
