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
import androidx.navigation.NavController
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

// IDs de hábitos ahora son únicos para evitar que se marquen todos juntos
data class Habit(
    val id: Int = Random.nextInt(),
    var name: String
)

// --- MAIN SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDeEstudiosScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showFullCalendar by remember { mutableStateOf(false) }

    val materias = remember { mutableStateListOf<Materia>() }
    val tareas = remember { mutableStateListOf<Tarea>() }

    var showAddMateriaDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // --- State para Hábitos ---
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

    // Para eliminar hábito con confirmación
    var habitToDelete by remember { mutableStateOf<Habit?>(null) }
    var showDeleteHabitDialog by remember { mutableStateOf(false) }

    // --- State para edición / borrado de tareas ---
    var taskToEdit by remember { mutableStateOf<Tarea?>(null) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Tarea?>(null) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }

    // --- State para Snackbar ---
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = GradientEnd,
        topBar = { TopBar(navController) },
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
                    onHeaderClick = { showFullCalendar = true }
                )
            }

            item {
                HorizontalCalendar(selectedDate) { newDate -> selectedDate = newDate }
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    color = ButtonPrimary.copy(alpha = 0.5f)
                )
            }

            item {
                Header("Actividades para ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM"))}")
            }

            if (materias.isEmpty()) {
                item { EmptyState() }
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
                            }
                        )
                    }
                }
            }

            item { Header("Hábitos del día") }

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
                    }
                )
            }

            item {
                DailyProgress(
                    tareas = tareas.filter { it.fechaEntrega.isEqual(selectedDate) },
                    totalHabitos = habits.size,
                    habitosCompletos = completedHabitsForSelectedDate.size
                )
            }

            item {
                AIAssistantCard(
                    tareas = tareas.filter { it.fechaEntrega.isEqual(selectedDate) },
                    habits = habits,
                    completedHabits = completedHabitsForSelectedDate
                )
            }
        }
    }

    // --- Diálogo para añadir Materia ---
    if (showAddMateriaDialog) {
        AddMateriaDialog(
            onDismiss = { showAddMateriaDialog = false },
            onMateriaAdd = {
                materias.add(it)
                showAddMateriaDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Materia agregada con éxito")
                }
            }
        )
    }

    // --- Diálogo para añadir Tarea ---
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
            }
        )
    }

    // --- Diálogo para editar Tarea ---
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
            }
        )
    }

    // --- Diálogo de confirmación para eliminar Tarea ---
    if (showDeleteTaskDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteTaskDialog = false
                taskToDelete = null
            },
            title = {
                Text(
                    text = "Eliminar tarea",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar esta tarea? Esta acción no se puede deshacer.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        taskToDelete?.let { tarea ->
                            tareas.removeAll { it.id == tarea.id }
                            scope.launch {
                                snackbarHostState.showSnackbar("Tarea eliminada con éxito")
                            }
                        }
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4D4F)
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    },
                    shape = RoundedCornerShape(50),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Cancelar", color = LinkGreen)
                }
            },
            containerColor = ButtonPrimary.copy(alpha = 0.98f)
        )
    }

    // --- Diálogo de confirmación para eliminar Hábito ---
    if (showDeleteHabitDialog && habitToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteHabitDialog = false
                habitToDelete = null
            },
            title = {
                Text(
                    text = "Eliminar hábito",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Seguro que deseas eliminar este hábito? Esta acción no se puede deshacer.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        habitToDelete?.let { habit ->
                            habits.remove(habit)
                            completedHabitsByDate.values.forEach { it.remove(habit.id) }
                            scope.launch {
                                snackbarHostState.showSnackbar("Hábito eliminado con éxito")
                            }
                        }
                        showDeleteHabitDialog = false
                        habitToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4D4F)
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteHabitDialog = false
                        habitToDelete = null
                    },
                    shape = RoundedCornerShape(50),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Cancelar", color = LinkGreen)
                }
            },
            containerColor = ButtonPrimary.copy(alpha = 0.98f)
        )
    }

    // --- Diálogo para añadir / editar Hábito ---
    if (showHabitDialog) {
        AddOrEditHabitDialog(
            habit = habitToEdit,
            onDismiss = { showHabitDialog = false },
            onConfirm = { updatedHabit, isNew ->
                if (isNew) {
                    habits.add(updatedHabit)
                    scope.launch {
                        snackbarHostState.showSnackbar("Hábito creado con éxito")
                    }
                } else {
                    val index = habits.indexOfFirst { it.id == updatedHabit.id }
                    if (index != -1) {
                        habits[index] = updatedHabit
                        scope.launch {
                            snackbarHostState.showSnackbar("Hábito editado con éxito")
                        }
                    }
                }
                showHabitDialog = false
            }
        )
    }

    // --- Diálogo de calendario completo ---
    if (showFullCalendar) {
        FullCalendarDialog(
            selectedDate = selectedDate,
            onDateSelected = { newDate ->
                selectedDate = newDate
                showFullCalendar = false
            },
            onDismiss = { showFullCalendar = false }
        )
    }
}

// ---------- COMPONENTES MODIFICADOS Y NUEVOS ----------

@Composable
fun HabitSection(
    habits: List<Habit>,
    completedHabits: Set<Int>,
    onHabitClicked: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
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
                    colors = CheckboxDefaults.colors(checkedColor = LinkGreen)
                )
                Text(
                    text = habit.name,
                    color = if (isCompleted) TextSecondary else Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f)
                )
                IconButton(onClick = { onEditHabit(habit) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar Hábito",
                        tint = TextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { onDeleteHabit(habit) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar Hábito",
                        tint = TextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyProgress(
    tareas: List<Tarea>,
    totalHabitos: Int,
    habitosCompletos: Int
) {
    // Lógica nueva: nada de 50% fantasma
    val progresoTotal = remember(tareas, totalHabitos, habitosCompletos) {
        val hayTareas = tareas.isNotEmpty()
        val hayHabitos = totalHabitos > 0

        when {
            // No hay nada
            !hayTareas && !hayHabitos -> 0f

            // Solo hábitos
            !hayTareas && hayHabitos ->
                habitosCompletos.toFloat() / totalHabitos.coerceAtLeast(1)

            // Solo tareas
            hayTareas && !hayHabitos ->
                tareas.count { it.completada }.toFloat() / tareas.size.coerceAtLeast(1)

            // Ambas categorías
            else -> {
                val pTareas =
                    tareas.count { it.completada }.toFloat() / tareas.size.coerceAtLeast(1)
                val pHabitos =
                    habitosCompletos.toFloat() / totalHabitos.coerceAtLeast(1)
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
            color = LinkGreen,
            trackColor = ButtonPrimary.copy(alpha = 0.3f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Progreso total: ${(progresoTotal * 100).toInt().coerceIn(0, 100)}%",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        if (progresoTotal >= 1f) {
            Text(
                "¡Excelente! Cumpliste todas tus metas de hoy 🎉",
                color = LinkGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AIAssistantCard(
    tareas: List<Tarea>,
    habits: List<Habit>,
    completedHabits: Set<Int>
) {
    val incompleteTasks = tareas.count { !it.completada }
    val incompleteHabits = habits.count { it.id !in completedHabits }
    val uncompletedHabitNames =
        habits.filter { it.id !in completedHabits }.map { it.name }

    val suggestion = remember(incompleteTasks, incompleteHabits) {
        when {
            incompleteTasks > 2 ->
                "Tienes varias tareas pendientes. ¡Concéntrate y empieza por la más importante!"
            uncompletedHabitNames.any { it.contains("descansar", ignoreCase = true) } ->
                "No te olvides de tomar un descanso. Tu mente te lo agradecerá."
            incompleteTasks > 0 ->
                "¡Ánimo! Ya casi terminas tus tareas de hoy."
            incompleteHabits > 0 ->
                "Ya completaste tus tareas, ahora enfócate en tus hábitos para un día perfecto."
            else ->
                "¡Felicidades! Has completado todas tus tareas y hábitos de hoy. ¡A disfrutar tu tiempo libre! 🎉"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ButtonPrimary.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sugerencia del día 🌞",
                color = LinkGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(suggestion, color = Color.White)
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
                SmallFloatingActionButton(
                    onClick = {
                        onAddTask()
                        isExpanded = false
                    },
                    containerColor = ButtonPrimary
                ) {
                    Icon(Icons.Default.Event, "Añadir Tarea", tint = Color.White)
                }
                SmallFloatingActionButton(
                    onClick = {
                        onAddMateria()
                        isExpanded = false
                    },
                    containerColor = ButtonPrimary
                ) {
                    Icon(Icons.Default.Book, "Añadir Materia", tint = Color.White)
                }
                SmallFloatingActionButton(
                    onClick = {
                        onAddHabit()
                        isExpanded = false
                    },
                    containerColor = ButtonPrimary
                ) {
                    Icon(Icons.Default.Sync, "Añadir Hábito", tint = Color.White)
                }
            }
        }
        FloatingActionButton(
            onClick = { isExpanded = !isExpanded },
            containerColor = LinkGreen
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Abrir menú de acciones",
                tint = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddOrEditHabitDialog(
    habit: Habit?,
    onDismiss: () -> Unit,
    onConfirm: (habit: Habit, isNew: Boolean) -> Unit
) {
    val isEditing = habit != null
    var name by remember { mutableStateOf(habit?.name ?: "") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ButtonPrimary.copy(alpha = 0.98f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isEditing) "Editar Hábito" else "Añadir Nuevo Hábito",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isError = false
                    },
                    label = { Text("Nombre del hábito") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) Text(
                            "El nombre no puede estar vacío",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Cancelar", color = LinkGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                isError = true
                            } else {
                                val newHabit = habit?.copy(name = name) ?: Habit(name = name)
                                onConfirm(newHabit, !isEditing)
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinkGreen
                        )
                    ) {
                        Text("Guardar", color = Color.Black)
                    }
                }
            }
        }
    }
}

// --- Componentes Antiguos (algunos con cambios) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Plan de Estudios", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "Regresar",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun CalendarHeader(
    date: LocalDate,
    onHeaderClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-ES"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() } // abre calendario completo
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // Título del mes (igual que antes)
        Text(
            date.format(formatter).replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Línea + icono de calendario a la derecha
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.2.dp)
                    .background(ButtonPrimary.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Calendario",
                tint = LinkGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun HorizontalCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val week = remember(selectedDate) {
        (-3..3).map { selectedDate.plusDays(it.toLong()) }
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(week, key = { it.toEpochDay() }) { date ->
            DayCard(date, date == selectedDate) { onDateSelected(date) }
        }
    }
}

@Composable
private fun DayCard(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayOfWeekFormatter =
        DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-ES"))
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ButtonPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            date.format(dayOfWeekFormatter).uppercase(),
            fontSize = 12.sp,
            color = if (isSelected) Color.White else TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            date.dayOfMonth.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else PrimaryTextColor
        )
    }
}

@Composable
private fun Header(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun EmptyState() {
    Text(
        "Aún no tienes materias. ¡Toca el botón + para empezar!",
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        textAlign = TextAlign.Center,
        color = TextSecondary
    )
}

@Composable
fun MateriaCard(
    materia: Materia,
    tareas: List<Tarea>,
    onAddTaskClick: () -> Unit,
    onTareaCheckedChange: (Tarea, Boolean) -> Unit,
    onEditTaskClick: (Tarea) -> Unit,
    onDeleteTaskClick: (Tarea) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ButtonPrimary.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(materia.color, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        materia.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (materia.profesor.isNotBlank()) {
                        Text(
                            materia.profesor,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(onClick = onAddTaskClick) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Añadir tarea",
                        tint = LinkGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (tareas.isNotEmpty()) {
                tareas.sortedBy { it.fechaEntrega }.forEach { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onCheckedChange = onTareaCheckedChange,
                        onEditClick = { onEditTaskClick(tarea) },
                        onDeleteClick = { onDeleteTaskClick(tarea) }
                    )
                }
            } else {
                Text(
                    "No hay tareas para hoy.",
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TareaItem(
    tarea: Tarea,
    onCheckedChange: (Tarea, Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
            colors = CheckboxDefaults.colors(
                checkedColor = LinkGreen,
                uncheckedColor = TextSecondary,
                checkmarkColor = GradientEnd
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                tarea.descripcion,
                color = if (tarea.completada) TextSecondary else Color.White
            )
            Text(
                "Vence: ${tarea.fechaEntrega.format(DateTimeFormatter.ofPattern("dd MMM"))}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(urgencyColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onEditClick) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Editar tarea",
                tint = TextSecondary
            )
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar tarea",
                tint = Color(0xFFE57373)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMateriaDialog(
    onDismiss: () -> Unit,
    onMateriaAdd: (Materia) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var isNombreError by remember { mutableStateOf(false) }
    var profesor by remember { mutableStateOf("") }

    val colorOptions = remember {
        listOf(
            Color(0xFFF44336),
            Color(0xFF4CAF50),
            Color(0xFF2196F3),
            Color(0xFFFFEB3B),
            Color(0xFF9C27B0)
        )
    }
    var selectedColor by remember { mutableStateOf(colorOptions[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ButtonPrimary.copy(alpha = 0.98f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Añadir Nueva Materia",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        isNombreError = false
                    },
                    label = { Text("Nombre de la Materia") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isNombreError,
                    supportingText = {
                        if (isNombreError) Text(
                            "El nombre no puede estar vacío",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = profesor,
                    onValueChange = { profesor = it },
                    label = { Text("Profesor o Grupo (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Elige un color:",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = TextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = color }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Cancelar", color = LinkGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nombre.isBlank()) {
                                isNombreError = true
                            } else {
                                onMateriaAdd(
                                    Materia(
                                        nombre = nombre,
                                        profesor = profesor,
                                        color = selectedColor
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinkGreen
                        )
                    ) {
                        Text("Guardar", color = Color.Black)
                    }
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
    onTaskAdd: (Tarea) -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var isDescriptionError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedMateria by remember { mutableStateOf(materias.firstOrNull()) }

    val showDatePicker = remember { mutableStateOf(false) }
    var taskDate by remember { mutableStateOf(LocalDate.now()) }

    if (materias.isEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ButtonPrimary.copy(alpha = 0.98f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Primero añade una materia",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text("Entendido")
                    }
                }
            }
        }
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ButtonPrimary.copy(alpha = 0.98f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Añadir Nueva Tarea",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMateria?.nombre ?: "Selecciona una materia",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Materia") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LinkGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = LinkGreen,
                            focusedLabelColor = LinkGreen,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        materias.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(materia.nombre) },
                                onClick = {
                                    selectedMateria = materia
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                        isDescriptionError = false
                    },
                    label = { Text("Descripción de la tarea") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDescriptionError,
                    supportingText = {
                        if (isDescriptionError) Text(
                            "La descripción no puede estar vacía",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showDatePicker.value = true }) {
                    Text(
                        "Fecha de Entrega: ${
                            taskDate.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )
                        }"
                    )
                }
                if (showDatePicker.value) {
                    val datePickerState =
                        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker.value = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    taskDate = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                                }
                                showDatePicker.value = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker.value = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Cancelar", color = LinkGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (descripcion.isBlank()) {
                                isDescriptionError = true
                            } else if (selectedMateria != null) {
                                onTaskAdd(
                                    Tarea(
                                        materiaId = selectedMateria!!.id,
                                        descripcion = descripcion,
                                        fechaEntrega = taskDate
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinkGreen
                        )
                    ) {
                        Text("Añadir", color = Color.Black)
                    }
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
    onTaskUpdated: (Tarea) -> Unit
) {
    var descripcion by remember { mutableStateOf(tareaOriginal.descripcion) }
    var isDescriptionError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedMateria by remember {
        mutableStateOf(
            materias.firstOrNull { it.id == tareaOriginal.materiaId }
        )
    }

    val showDatePicker = remember { mutableStateOf(false) }
    var taskDate by remember { mutableStateOf(tareaOriginal.fechaEntrega) }

    if (materias.isEmpty()) {
        onDismiss()
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = ButtonPrimary.copy(alpha = 0.98f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Editar Tarea",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedMateria?.nombre ?: "Selecciona una materia",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Materia") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LinkGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = LinkGreen,
                            focusedLabelColor = LinkGreen,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        materias.forEach { materia ->
                            DropdownMenuItem(
                                text = { Text(materia.nombre) },
                                onClick = {
                                    selectedMateria = materia
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                        isDescriptionError = false
                    },
                    label = { Text("Descripción de la tarea") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDescriptionError,
                    supportingText = {
                        if (isDescriptionError) Text(
                            "La descripción no puede estar vacía",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = TextSecondary,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showDatePicker.value = true }) {
                    Text(
                        "Fecha de Entrega: ${
                            taskDate.format(
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )
                        }"
                    )
                }
                if (showDatePicker.value) {
                    val datePickerState =
                        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker.value = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    taskDate = LocalDate.ofEpochDay(millis / (1000 * 60 * 60 * 24))
                                }
                                showDatePicker.value = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker.value = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                    ) {
                        Text("Cancelar", color = LinkGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (descripcion.isBlank()) {
                                isDescriptionError = true
                            } else if (selectedMateria != null) {
                                onTaskUpdated(
                                    tareaOriginal.copy(
                                        materiaId = selectedMateria!!.id,
                                        descripcion = descripcion,
                                        fechaEntrega = taskDate
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinkGreen
                        )
                    ) {
                        Text("Guardar cambios", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullCalendarDialog(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    val monthName = currentMonth.month.getDisplayName(
        TextStyle.FULL,
        Locale.forLanguageTag("es-ES")
    ).replaceFirstChar { it.titlecase() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOffset = currentMonth.atDay(1).dayOfWeek.value - 1

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GradientEnd)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            "Mes anterior",
                            tint = Color.White
                        )
                    }
                    Text(
                        "$monthName ${currentMonth.year}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            "Mes siguiente",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")
                    items(weekDays.size) { index ->
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                weekDays[index],
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    items(firstDayOffset) {
                        Box(
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    items(daysInMonth) { index ->
                        val day = index + 1
                        val date = currentMonth.atDay(day)
                        val isSelected = date == selectedDate
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ButtonPrimary else Color.Transparent
                                )
                                .clickable { onDateSelected(date) }
                        ) {
                            Text(
                                day.toString(),
                                color = if (isSelected) Color.White else PrimaryTextColor
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonPrimary
                        )
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
            }
        }
    }
}
