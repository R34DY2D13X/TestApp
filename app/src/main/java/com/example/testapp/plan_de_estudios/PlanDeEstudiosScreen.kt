package com.example.testapp.plan_de_estudios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

// --- DATA MODELS ---
data class Materia(
    val id: Int = System.currentTimeMillis().toInt(),
    val nombre: String,
    val profesor: String,
    val color: Color
)

data class Tarea(
    val id: Int = System.currentTimeMillis().toInt() + 1,
    val materiaId: Int,
    val descripcion: String,
    val fechaEntrega: LocalDate,
    var completada: Boolean = false
)

data class Habit(
    val id: Int = Random.nextInt(),
    var name: String
)

// --- MAIN SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDeEstudiosScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val settings by settingsViewModel.uiState.collectAsState()
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showFullCalendar by remember { mutableStateOf(false) }

    val materias = remember { mutableStateListOf<Materia>() }
    val tareas = remember { mutableStateListOf<Tarea>() }

    var showAddMateriaDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val habits = remember {
        mutableStateListOf(
            Habit(name = "Dormir 8 horas"),
            Habit(name = "Beber 2L de agua"),
            Habit(name = "Descansar la vista cada 30 min")
        )
    }
    val completedHabitsByDate = remember { mutableStateMapOf<LocalDate, MutableSet<Int>>() }
    var habitToEdit by remember { mutableStateOf<Habit?>(null) }
    var showHabitDialog by remember { mutableStateOf(false) }

    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    var showDeleteHabitDialog by remember { mutableStateOf(false) }

    var taskToEdit by remember { mutableStateOf<Tarea?>(null) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Tarea?>(null) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val dynamicBg = if (highContrast) Color.Black else GradientEnd
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.4f)
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary

    Scaffold(
        containerColor = dynamicBg,
        topBar = { TopBar(navController, fontSize, dynamicPrimaryText) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            SpeedDialFab(
                onAddMateria = { showAddMateriaDialog = true },
                onAddTask = { showAddTaskDialog = true },
                onAddHabit = {
                    habitToEdit = null
                    showHabitDialog = true
                }
            )
        }
    ) { padding ->
        val completedHabitsForSelectedDate =
            completedHabitsByDate.getOrPut(selectedDate) { mutableSetOf() }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                CalendarHeader(
                    date = selectedDate,
                    onHeaderClick = { showFullCalendar = true },
                    fontSize = fontSize,
                    textColor = dynamicPrimaryText,
                    accentColor = if (highContrast) Color.Yellow else LinkGreen
                )
            }

            item {
                HorizontalCalendar(selectedDate, fontSize, highContrast) { newDate -> selectedDate = newDate }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    color = ButtonPrimary.copy(alpha = 0.5f)
                )
            }

            item {
                Header("Actividades para ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM"))}", fontSize, dynamicSecondaryText)
            }

            if (materias.isEmpty()) {
                item { EmptyState(fontSize, dynamicSecondaryText) }
            } else {
                items(materias, key = { it.id }) { materia ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        MateriaCard(
                            materia = materia,
                            tareas = tareas.filter {
                                it.materiaId == materia.id &&
                                        it.fechaEntrega.isEqual(selectedDate)
                            },
                            onAddTaskClick = { showAddTaskDialog = true },
                            onTareaCheckedChange = { tarea, isChecked ->
                                val index = tareas.indexOfFirst { it.id == tarea.id }
                                if (index != -1) {
                                    tareas[index] = tarea.copy(completada = isChecked)
                                }
                            },
                            onEditTaskClick = { tarea ->
                                taskToEdit = tarea
                                showEditTaskDialog = true
                            },
                            onDeleteTaskClick = { tarea ->
                                taskToDelete = tarea
                                showDeleteTaskDialog = true
                            },
                            settings = settings
                        )
                    }
                }
            }

            item { Header("Hábitos del día", fontSize, dynamicSecondaryText) }

            item {
                HabitSection(
                    habits = habits,
                    completedHabits = completedHabitsForSelectedDate,
                    onHabitClicked = { habit ->
                        val currentHabits =
                            completedHabitsByDate.getOrPut(selectedDate) { mutableSetOf() }
                        val newDayHabits = currentHabits.toMutableSet()

                        if (habit.id in newDayHabits) {
                            newDayHabits.remove(habit.id)
                        } else {
                            newDayHabits.add(habit.id)
                        }
                        completedHabitsByDate[selectedDate] = newDayHabits
                    },
                    onEditHabit = {
                        habitToEdit = it
                        showHabitDialog = true
                    },
                    onDeleteHabit = { habitToDeleteParam ->
                        habitToDelete = habitToDeleteParam
                        showDeleteHabitDialog = true
                    },
                    settings = settings
                )
            }

            item {
                DailyProgress(
                    tareas = tareas.filter { it.fechaEntrega.isEqual(selectedDate) },
                    totalHabitos = habits.size,
                    habitosCompletos = completedHabitsForSelectedDate.size,
                    settings = settings
                )
            }

            item {
                AIAssistantCard(
                    tareas = tareas.filter { it.fechaEntrega.isEqual(selectedDate) },
                    habits = habits,
                    completedHabits = completedHabitsForSelectedDate,
                    settings = settings
                )
            }
        }
    }

    if (showAddMateriaDialog) {
        AddMateriaDialog(
            onDismiss = { showAddMateriaDialog = false },
            onMateriaAdd = {
                materias.add(it)
                showAddMateriaDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Materia agregada con éxito")
                }
            },
            settings = settings
        )
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            materias = materias,
            onDismiss = { showAddTaskDialog = false },
            onTaskAdd = {
                tareas.add(it)
                showAddTaskDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Tarea agregada con éxito")
                }
            },
            settings = settings
        )
    }

    if (showEditTaskDialog && taskToEdit != null) {
        EditTaskDialog(
            materias = materias,
            tareaOriginal = taskToEdit!!,
            onDismiss = {
                showEditTaskDialog = false
                taskToEdit = null
            },
            onTaskUpdated = { updatedTask ->
                val index = tareas.indexOfFirst { it.id == updatedTask.id }
                if (index != -1) {
                    tareas[index] = updatedTask
                    scope.launch {
                        snackbarHostState.showSnackbar("Tarea actualizada con éxito")
                    }
                }
                showEditTaskDialog = false
                taskToEdit = null
            },
            settings = settings
        )
    }

    if (showDeleteTaskDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteTaskDialog = false
                taskToDelete = null
            },
            title = { Text("Eliminar tarea", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp * fontSize) },
            text = { Text("¿Seguro que quieres eliminar esta tarea? Esta acción no se puede deshacer.", color = dynamicSecondaryText, fontSize = 16.sp * fontSize) },
            confirmButton = { Button(onClick = { taskToDelete?.let { tarea -> tareas.removeAll { it.id == tarea.id }; scope.launch { snackbarHostState.showSnackbar("Tarea eliminada con éxito") } }; showDeleteTaskDialog = false; taskToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4F)), shape = RoundedCornerShape(50)) { Text("Eliminar", color = Color.White, fontSize = 14.sp * fontSize) } },
            dismissButton = { OutlinedButton(onClick = { showDeleteTaskDialog = false; taskToDelete = null }, shape = RoundedCornerShape(50), border = ButtonDefaults.outlinedButtonBorder) { Text("Cancelar", color = LinkGreen, fontSize = 14.sp * fontSize) } },
            containerColor = dynamicCardBg.copy(alpha = 0.98f)
        )
    }

    if (showDeleteHabitDialog && habitToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteHabitDialog = false; habitToDelete = null },
            title = { Text("Eliminar hábito", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp * fontSize) },
            text = { Text("¿Seguro que deseas eliminar este hábito? Esta acción no se puede deshacer.", color = dynamicSecondaryText, fontSize = 16.sp * fontSize) },
            confirmButton = { Button(onClick = { habitToDelete?.let { habit -> habits.remove(habit); completedHabitsByDate.values.forEach { it.remove(habit.id) }; scope.launch { snackbarHostState.showSnackbar("Hábito eliminado con éxito") } }; showDeleteHabitDialog = false; habitToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4F)), shape = RoundedCornerShape(50)) { Text("Eliminar", color = Color.White, fontSize = 14.sp * fontSize) } },
            dismissButton = { OutlinedButton(onClick = { showDeleteHabitDialog = false; habitToDelete = null }, shape = RoundedCornerShape(50), border = ButtonDefaults.outlinedButtonBorder) { Text("Cancelar", color = LinkGreen, fontSize = 14.sp * fontSize) } },
            containerColor = dynamicCardBg.copy(alpha = 0.98f)
        )
    }

    if (showHabitDialog) {
        AddOrEditHabitDialog(
            habit = habitToEdit,
            onDismiss = { showHabitDialog = false },
            onConfirm = { updatedHabit, isNew ->
                if (isNew) {
                    habits.add(updatedHabit)
                    scope.launch { snackbarHostState.showSnackbar("Hábito creado con éxito") }
                } else {
                    val index = habits.indexOfFirst { it.id == updatedHabit.id }
                    if (index != -1) {
                        habits[index] = updatedHabit
                        scope.launch { snackbarHostState.showSnackbar("Hábito editado con éxito") }
                    }
                }
                showHabitDialog = false
            },
            settings = settings
        )
    }

    if (showFullCalendar) {
        FullCalendarDialog(
            selectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
                showFullCalendar = false
            },
            onDismiss = { showFullCalendar = false },
            settings = settings
        )
    }
}

@Composable
fun HabitSection(
    habits: List<Habit>,
    completedHabits: Set<Int>,
    onHabitClicked: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        habits.forEach { habit ->
            val isCompleted = habit.id in completedHabits
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onHabitClicked(habit) },
                    colors = CheckboxDefaults.colors(checkedColor = if (highContrast) Color.Yellow else LinkGreen)
                )
                Text(
                    text = habit.name,
                    color = if (isCompleted) dynamicSecondaryText else dynamicPrimaryText,
                    fontSize = 16.sp * fontSize,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                IconButton(onClick = { onEditHabit(habit) }) {
                    Icon(Icons.Default.Edit, "Editar Hábito", tint = dynamicSecondaryText.copy(alpha = 0.7f), modifier = Modifier.size(20.dp * fontSize))
                }
                IconButton(onClick = { onDeleteHabit(habit) }) {
                    Icon(Icons.Default.Delete, "Eliminar Hábito", tint = dynamicSecondaryText.copy(alpha = 0.7f), modifier = Modifier.size(20.dp * fontSize))
                }
            }
        }
    }
}

@Composable
fun DailyProgress(
    tareas: List<Tarea>,
    totalHabitos: Int,
    habitosCompletos: Int,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val accentColor = if (highContrast) Color.Yellow else LinkGreen

    val progresoTotal = remember(tareas, totalHabitos, habitosCompletos) {
        val hayTareas = tareas.isNotEmpty()
        val hayHabitos = totalHabitos > 0
        when {
            !hayTareas && !hayHabitos -> 0f
            !hayTareas && hayHabitos -> habitosCompletos.toFloat() / totalHabitos.coerceAtLeast(1)
            hayTareas && !hayHabitos -> tareas.count { it.completada }.toFloat() / tareas.size.coerceAtLeast(1)
            else -> {
                val pTareas = tareas.count { it.completada }.toFloat() / tareas.size.coerceAtLeast(1)
                val pHabitos = habitosCompletos.toFloat() / totalHabitos.coerceAtLeast(1)
                (pTareas + pHabitos) / 2f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { progresoTotal.coerceIn(0f, 1f) },
            color = accentColor,
            trackColor = ButtonPrimary.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Progreso total: ${(progresoTotal * 100).toInt().coerceIn(0, 100)}%",
            color = dynamicPrimaryText,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp * fontSize
        )
        if (progresoTotal >= 1f) {
            Text(
                "¡Excelente! Cumpliste todas tus metas de hoy 🎉",
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp * fontSize,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AIAssistantCard(
    tareas: List<Tarea>,
    habits: List<Habit>,
    completedHabits: Set<Int>,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.4f)

    val incompleteTasks = tareas.count { !it.completada }
    val incompleteHabits = habits.count { it.id !in completedHabits }
    val uncompletedHabitNames = habits.filter { it.id !in completedHabits }.map { it.name }

    val suggestion = remember(incompleteTasks, incompleteHabits) {
        when {
            incompleteTasks > 2 -> "Tienes varias tareas pendientes. ¡Concéntrate y empieza por la más importante!"
            uncompletedHabitNames.any { it.contains("descansar", ignoreCase = true) } -> "No te olvides de tomar un descanso. Tu mente te lo agradecerá."
            incompleteTasks > 0 -> "¡Ánimo! Ya casi terminas tus tareas de hoy."
            incompleteHabits > 0 -> "Ya completaste tus tareas, ahora enfócate en tus hábitos para un día perfecto."
            else -> "¡Felicidades! Has completado todas tus tareas y hábitos de hoy. ¡A disfrutar tu tiempo libre! 🎉"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = dynamicCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sugerencia del día 🌞",
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp * fontSize
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(suggestion, color = dynamicPrimaryText, fontSize = 14.sp * fontSize)
        }
    }
}

@Composable
fun SpeedDialFab(
    onAddMateria: () -> Unit,
    onAddTask: () -> Unit,
    onAddHabit: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(visible = isExpanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallFloatingActionButton(onClick = { onAddTask(); isExpanded = false }, containerColor = ButtonPrimary) { Icon(Icons.Default.Event, "Añadir Tarea", tint = Color.White) }
                SmallFloatingActionButton(onClick = { onAddMateria(); isExpanded = false }, containerColor = ButtonPrimary) { Icon(Icons.Default.Book, "Añadir Materia", tint = Color.White) }
                SmallFloatingActionButton(onClick = { onAddHabit(); isExpanded = false }, containerColor = ButtonPrimary) { Icon(Icons.Default.Sync, "Añadir Hábito", tint = Color.White) }
            }
        }
        FloatingActionButton(onClick = { isExpanded = !isExpanded }, containerColor = LinkGreen) {
            Icon(imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add, contentDescription = "Abrir menú de acciones", tint = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditHabitDialog(
    habit: Habit?,
    onDismiss: () -> Unit,
    onConfirm: (habit: Habit, isNew: Boolean) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.98f)

    val isEditing = habit != null
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = if (isEditing) "Editar Hábito" else "Añadir Nuevo Hábito", style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = dynamicPrimaryText)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; isError = false },
                    label = { Text("Nombre del hábito", fontSize = 16.sp * fontSize) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isError,
                    supportingText = { if (isError) Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error, fontSize = 14.sp * fontSize) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("Cancelar", color = accentColor, fontSize = 14.sp * fontSize) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (name.isBlank()) { isError = true } else { val newHabit = habit?.copy(name = name) ?: Habit(name = name); onConfirm(newHabit, !isEditing) } }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Guardar", color = Color.Black, fontSize = 14.sp * fontSize) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    fontSize: Float,
    textColor: Color
) {
    TopAppBar(
        title = { Text("Plan de Estudios", color = textColor, fontSize = 20.sp * fontSize) },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar", tint = textColor) } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun CalendarHeader(
    date: LocalDate,
    onHeaderClick: () -> Unit,
    fontSize: Float,
    textColor: Color,
    accentColor: Color
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-ES"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(date.format(formatter).replaceFirstChar { it.titlecase() }, style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = textColor, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).height(1.2.dp).background(ButtonPrimary.copy(alpha = 0.5f)))
            Spacer(modifier = Modifier.width(6.dp))
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Calendario", tint = accentColor, modifier = Modifier.size(18.dp * fontSize))
        }
    }
}

@Composable
private fun HorizontalCalendar(
    selectedDate: LocalDate,
    fontSize: Float,
    highContrast: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val week = remember(selectedDate) { (-3..3).map { selectedDate.plusDays(it.toLong()) } }
    val dynamicSelectedBg = if (highContrast) Color.Yellow else ButtonPrimary

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(week, key = { it.toEpochDay() }) { date ->
            DayCard(date, date == selectedDate, fontSize, highContrast) { onDateSelected(date) }
        }
    }
}

@Composable
private fun DayCard(
    date: LocalDate, 
    isSelected: Boolean, 
    fontSize: Float,
    highContrast: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-ES"))
    val dynamicSelectedBg = if (highContrast) Color.Yellow else ButtonPrimary
    val dynamicSelectedText = if (highContrast) Color.Black else Color.White
    val dynamicUnselectedText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicUnselectedSecondaryText = if (highContrast) Color.LightGray else TextSecondary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) dynamicSelectedBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(date.format(dayOfWeekFormatter).uppercase(), fontSize = 12.sp * fontSize, color = if (isSelected) dynamicSelectedText else dynamicUnselectedSecondaryText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(date.dayOfMonth.toString(), fontSize = 20.sp * fontSize, fontWeight = FontWeight.Bold, color = if (isSelected) dynamicSelectedText else dynamicUnselectedText)
    }
}

@Composable
private fun Header(text: String, fontSize: Float, textColor: Color) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize * fontSize),
        fontWeight = FontWeight.Bold,
        color = textColor,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun EmptyState(fontSize: Float, textColor: Color) {
    Text(
        "Aún no tienes materias. ¡Toca el botón + para empezar!",
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        textAlign = TextAlign.Center,
        color = textColor,
        fontSize = 16.sp * fontSize
    )
}

@Composable
fun MateriaCard(
    materia: Materia,
    tareas: List<Tarea>,
    onAddTaskClick: () -> Unit,
    onTareaCheckedChange: (Tarea, Boolean) -> Unit,
    onEditTaskClick: (Tarea) -> Unit,
    onDeleteTaskClick: (Tarea) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.4f)
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = dynamicCardBg)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(modifier = Modifier.size(12.dp).background(materia.color, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(materia.nombre, style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * fontSize), fontWeight = FontWeight.Bold, color = dynamicPrimaryText)
                    if (materia.profesor.isNotBlank()) {
                        Text(materia.profesor, style = MaterialTheme.typography.bodyMedium.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontSize), color = dynamicSecondaryText)
                    }
                }
                IconButton(onClick = onAddTaskClick) { Icon(Icons.Default.Add, contentDescription = "Añadir tarea", tint = if (highContrast) Color.Yellow else LinkGreen) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (tareas.isNotEmpty()) {
                tareas.sortedBy { it.fechaEntrega }.forEach { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCheckedChange = onTareaCheckedChange,
                        onEditClick = { onEditTaskClick(tarea) },
                        onDeleteClick = { onDeleteTaskClick(tarea) },
                        settings = settings
                    )
                }
            } else {
                Text("No hay tareas para hoy.", color = dynamicSecondaryText, modifier = Modifier.padding(horizontal = 16.dp), fontSize = 14.sp * fontSize)
            }
        }
    }
}

@Composable
private fun TareaItem(
    tarea: Tarea,
    onCheckedChange: (Tarea, Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor

    val today = LocalDate.now()
    val urgencyColor = when {
        tarea.fechaEntrega < today && !tarea.completada -> Color(0xFFE57373)
        tarea.fechaEntrega == today -> Color(0xFFFFA726)
        tarea.fechaEntrega == today.plusDays(1) -> Color(0xFFFFD54F)
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = tarea.completada,
            onCheckedChange = { isChecked -> onCheckedChange(tarea, isChecked) },
            colors = CheckboxDefaults.colors(checkedColor = if (highContrast) Color.Yellow else LinkGreen, uncheckedColor = dynamicSecondaryText, checkmarkColor = if (highContrast) Color.Black else GradientEnd)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tarea.descripcion, color = if (tarea.completada) dynamicSecondaryText else dynamicPrimaryText, fontSize = 16.sp * fontSize)
            Text("Vence: ${tarea.fechaEntrega.format(DateTimeFormatter.ofPattern("dd MMM"))}", color = dynamicSecondaryText, style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * fontSize))
        }
        Box(modifier = Modifier.size(12.dp).background(urgencyColor, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, "Editar tarea", tint = dynamicSecondaryText) }
        IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, "Eliminar tarea", tint = Color(0xFFE57373)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMateriaDialog(
    onDismiss: () -> Unit,
    onMateriaAdd: (Materia) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.98f)

    var nombre by remember { mutableStateOf("") }
    var isNombreError by remember { mutableStateOf(false) }
    var profesor by remember { mutableStateOf("") }

    val colorOptions = remember { listOf(Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFFEB3B), Color(0xFF9C27B0)) }
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Añadir Nueva Materia", style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = dynamicPrimaryText)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; isNombreError = false },
                    label = { Text("Nombre de la Materia", fontSize = 16.sp * fontSize) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isNombreError,
                    supportingText = { if (isNombreError) Text("El nombre no puede estar vacío", color = MaterialTheme.colorScheme.error, fontSize = 14.sp * fontSize) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = profesor,
                    onValueChange = { profesor = it },
                    label = { Text("Profesor o Grupo (Opcional)", fontSize = 16.sp * fontSize) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Elige un color:", modifier = Modifier.padding(bottom = 8.dp), color = dynamicSecondaryText, fontSize = 14.sp * fontSize)
                Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                    colorOptions.forEach { color -> Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(color).clickable { selectedColor = color }) }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("Cancelar", color = accentColor, fontSize = 14.sp * fontSize) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (nombre.isBlank()) { isNombreError = true } else { onMateriaAdd(Materia(nombre = nombre, profesor = profesor, color = selectedColor)) } }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Guardar", color = Color.Black, fontSize = 14.sp * fontSize) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    materias: List<Materia>,
    onDismiss: () -> Unit,
    onTaskAdd: (Tarea) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.98f)

    var descripcion by remember { mutableStateOf("") }
    var isDescriptionError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedMateria by remember { mutableStateOf(materias.firstOrNull()) }

    val showDatePicker = remember { mutableStateOf(false) }
    var taskDate by remember { mutableStateOf(LocalDate.now()) }

    if (materias.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Primero añade una materia", style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = dynamicPrimaryText, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Entendido", fontSize = 14.sp * fontSize, color = Color.Black) }
                }
            }
        }
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Añadir Nueva Tarea", style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = dynamicPrimaryText)
                Spacer(modifier = Modifier.height(24.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = selectedMateria?.nombre ?: "Selecciona una materia", onValueChange = {}, readOnly = true, label = { Text("Materia", fontSize = 16.sp * fontSize) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        materias.forEach { materia -> DropdownMenuItem(text = { Text(materia.nombre) }, onClick = { selectedMateria = materia; expanded = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it; isDescriptionError = false },
                    label = { Text("Descripción de la tarea", fontSize = 16.sp * fontSize) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDescriptionError,
                    supportingText = { if (isDescriptionError) Text("La descripción no puede estar vacía", color = MaterialTheme.colorScheme.error, fontSize = 14.sp * fontSize) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showDatePicker.value = true }, colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.8f))) { Text("Fecha de Entrega: ${taskDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}", fontSize = 14.sp * fontSize, color = dynamicPrimaryText) }
                if (showDatePicker.value) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                    DatePickerDialog(onDismissRequest = { showDatePicker.value = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { millis -> taskDate = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24)) }; showDatePicker.value = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker.value = false }) { Text("Cancelar") } }) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("Cancelar", color = accentColor, fontSize = 14.sp * fontSize) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (descripcion.isBlank()) { isDescriptionError = true } else if (selectedMateria != null) { onTaskAdd(Tarea(materiaId = selectedMateria!!.id, descripcion = descripcion, fechaEntrega = taskDate)) } }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Añadir", color = Color.Black, fontSize = 14.sp * fontSize) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskDialog(
    materias: List<Materia>,
    tareaOriginal: Tarea,
    onDismiss: () -> Unit,
    onTaskUpdated: (Tarea) -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.98f)

    var descripcion by remember { mutableStateOf(tareaOriginal.descripcion) }
    var isDescriptionError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedMateria by remember { mutableStateOf(materias.firstOrNull { it.id == tareaOriginal.materiaId }) }

    val showDatePicker = remember { mutableStateOf(false) }
    var taskDate by remember { mutableStateOf(tareaOriginal.fechaEntrega) }

    if (materias.isEmpty()) {
        onDismiss()
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Editar Tarea", style = MaterialTheme.typography.headlineSmall.copy(fontSize = MaterialTheme.typography.headlineSmall.fontSize * fontSize), color = dynamicPrimaryText)
                Spacer(modifier = Modifier.height(24.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = selectedMateria?.nombre ?: "Selecciona una materia", onValueChange = {}, readOnly = true, label = { Text("Materia", fontSize = 16.sp * fontSize) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        materias.forEach { materia -> DropdownMenuItem(text = { Text(materia.nombre) }, onClick = { selectedMateria = materia; expanded = false }) }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it; isDescriptionError = false },
                    label = { Text("Descripción de la tarea", fontSize = 16.sp * fontSize) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDescriptionError,
                    supportingText = { if (isDescriptionError) Text("La descripción no puede estar vacía", color = MaterialTheme.colorScheme.error, fontSize = 14.sp * fontSize) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, unfocusedBorderColor = dynamicSecondaryText, focusedTextColor = dynamicPrimaryText, unfocusedTextColor = dynamicPrimaryText, cursorColor = accentColor, focusedLabelColor = accentColor, unfocusedLabelColor = dynamicSecondaryText)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showDatePicker.value = true }, colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.8f))) { Text("Fecha de Entrega: ${taskDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}", fontSize = 14.sp * fontSize, color = dynamicPrimaryText) }
                if (showDatePicker.value) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                    DatePickerDialog(onDismissRequest = { showDatePicker.value = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { millis -> taskDate = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24)) }; showDatePicker.value = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker.value = false }) { Text("Cancelar") } }) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("Cancelar", color = accentColor, fontSize = 14.sp * fontSize) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { if (descripcion.isBlank()) { isDescriptionError = true } else if (selectedMateria != null) { onTaskUpdated(tareaOriginal.copy(materiaId = selectedMateria!!.id, descripcion = descripcion, fechaEntrega = taskDate)) } }, shape = RoundedCornerShape(50), colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Guardar cambios", color = Color.Black, fontSize = 14.sp * fontSize) }
                }
            }
        }
    }
}

@Composable
private fun FullCalendarDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    settings: com.example.testapp.ajustes.SettingsState
) {
    val fontSize = settings.fontSize
    val highContrast = settings.highContrast
    val dynamicPrimaryText = if (highContrast) Color.White else PrimaryTextColor
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val accentColor = if (highContrast) Color.Yellow else LinkGreen
    val dynamicCardBg = if (highContrast) Color.Black else GradientEnd
    val dynamicSelectedBg = if (highContrast) Color.Yellow else ButtonPrimary

    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES")).replaceFirstChar { it.titlecase() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOffset = currentMonth.atDay(1).dayOfWeek.value - 1

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = dynamicCardBg)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior", tint = dynamicPrimaryText) }
                    Text("$monthName ${currentMonth.year}", color = dynamicPrimaryText, style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * fontSize), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente", tint = dynamicPrimaryText) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")
                    items(weekDays.size) { index -> Box(contentAlignment = Alignment.Center) { Text(weekDays[index], color = dynamicSecondaryText, fontWeight = FontWeight.Bold, fontSize = 12.sp * fontSize) } }
                    items(firstDayOffset) { Box(modifier = Modifier.size(40.dp)) }
                    items(daysInMonth) { index ->
                        val day = index + 1
                        val date = currentMonth.atDay(day)
                        val isSelected = date == selectedDate
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isSelected) dynamicSelectedBg else Color.Transparent).clickable { onDateSelected(date) }) {
                            Text(day.toString(), color = if (isSelected) Color.Black else dynamicPrimaryText, fontSize = 16.sp * fontSize)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = accentColor)) { Text("Cerrar", color = if (highContrast) Color.Black else Color.White, fontSize = 14.sp * fontSize) }
                }
            }
        }
    }
}