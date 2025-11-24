package com.example.testapp.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.testapp.ui.theme.ButtonPrimary
import com.example.testapp.ui.theme.GradientEnd
import com.example.testapp.ui.theme.LinkGreen
import com.example.testapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

// ----------------------------------------------------------------------
//  MODELOS PARA EL CALENDARIO
// ----------------------------------------------------------------------

enum class CalendarItemType {
    ESTUDIO,   // Tareas del plan de estudios
    HABITO,    // Hábitos
    PERSONAL   // Eventos personales
}

data class CalendarItem(
    val id: Int = Random.nextInt(),
    val date: LocalDate,
    val title: String,
    val description: String = "",
    val type: CalendarItemType
)

// ----------------------------------------------------------------------
//  PANTALLA PRINCIPAL DE CALENDARIO
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController) {

    // Mes y día seleccionados
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Lista de eventos del calendario (por ahora locales).
    // Más adelante se puede sustituir por un ViewModel compartido
    // con el módulo de Plan de Estudios / Hábitos.
    val calendarItems = remember { mutableStateListOf<CalendarItem>() }

    // Ejemplos iniciales
    LaunchedEffect(Unit) {
        if (calendarItems.isEmpty()) {
            val today = LocalDate.now()
            calendarItems.addAll(
                listOf(
                    CalendarItem(
                        date = today,
                        title = "Cuestionario de Redes",
                        description = "Plan de estudios",
                        type = CalendarItemType.ESTUDIO
                    ),
                    CalendarItem(
                        date = today,
                        title = "Beber 2L de agua",
                        description = "Hábito diario",
                        type = CalendarItemType.HABITO
                    ),
                    CalendarItem(
                        date = today.plusDays(1),
                        title = "Cita médica",
                        description = "Consulta general",
                        type = CalendarItemType.PERSONAL
                    )
                )
            )
        }
    }

    // Snackbar para mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado del diálogo de nuevo evento
    var showAddDialog by remember { mutableStateOf(false) }

    val monthName = currentMonth.month
        .getDisplayName(TextStyle.FULL, Locale("es"))
        .replaceFirstChar { it.uppercase() }

    Scaffold(
        containerColor = GradientEnd,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calendario",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Aseguramos que el diálogo siempre abra con el día seleccionado actual
                    showAddDialog = true
                },
                containerColor = LinkGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.Black)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // -------------------------------------------------
            // ENCABEZADO DEL MES
            // -------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Mes anterior",
                        tint = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$monthName ${currentMonth.year}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "Toca un día para ver tus pendientes",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Mes siguiente",
                        tint = Color.White
                    )
                }
            }

            // -------------------------------------------------
            // CABECERA DÍAS DE LA SEMANA
            // -------------------------------------------------
            val weekDays = listOf("L", "M", "X", "J", "V", "S", "D")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekDays.forEach {
                    Text(
                        text = it,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // -------------------------------------------------
            // GRID DEL CALENDARIO
            // -------------------------------------------------
            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayIndex =
                (currentMonth.atDay(1).dayOfWeek.value + 6) % 7 // Para que Lunes sea 0

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                // espacios vacíos antes del día 1
                items(firstDayIndex) {
                    Box(modifier = Modifier.size(44.dp))
                }

                items(daysInMonth) { index ->
                    val day = index + 1
                    val date = currentMonth.atDay(day)
                    val isSelected = date == selectedDate

                    // Eventos de este día
                    val itemsOfDay = calendarItems.filter { it.date == date }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedDate = date }
                            .background(
                                if (isSelected) ButtonPrimary.copy(alpha = 0.7f)
                                else Color.Transparent
                            )
                            .padding(top = 4.dp, bottom = 2.dp, start = 4.dp, end = 4.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = day.toString(),
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 17.sp
                            )

                            // Puntos de eventos
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                itemsOfDay.take(3).forEach { item ->
                                    val dotColor = when (item.type) {
                                        CalendarItemType.ESTUDIO -> ButtonPrimary
                                        CalendarItemType.HABITO -> LinkGreen
                                        CalendarItemType.PERSONAL -> Color(0xFFFFD54F)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // -------------------------------------------------
            // PANEL DE DETALLES DEL DÍA
            // -------------------------------------------------
            val detailFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
            val itemsSelectedDay =
                calendarItems.filter { it.date == selectedDate }.sortedBy { it.type.name }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                Text(
                    text = "Pendientes para ${selectedDate.format(detailFormatter)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )

                if (itemsSelectedDay.isEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No tienes nada registrado para este día.\nToca el botón + para agregar algo.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(itemsSelectedDay.size) { index ->
                            val item = itemsSelectedDay[index]
                            CalendarItemRow(item = item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------
    // DIÁLOGO PARA AGREGAR EVENTO
    // -------------------------------------------------
    if (showAddDialog) {
        AddCalendarItemDialog(
            initialDate = selectedDate,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                calendarItems.add(newItem)
                showAddDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Evento añadido al calendario")
                }
            }
        )
    }
}

// ----------------------------------------------------------------------
//  ROW PARA MOSTRAR UN EVENTO EN EL PANEL INFERIOR
// ----------------------------------------------------------------------

@Composable
private fun CalendarItemRow(item: CalendarItem) {
    val chipColor = when (item.type) {
        CalendarItemType.ESTUDIO -> ButtonPrimary
        CalendarItemType.HABITO -> LinkGreen
        CalendarItemType.PERSONAL -> Color(0xFFFFD54F)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ButtonPrimary.copy(alpha = 0.4f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(chipColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(chipColor.copy(alpha = 0.16f))
                .border(1.dp, chipColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = when (item.type) {
                    CalendarItemType.ESTUDIO -> "Estudio"
                    CalendarItemType.HABITO -> "Hábito"
                    CalendarItemType.PERSONAL -> "Personal"
                },
                color = chipColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ----------------------------------------------------------------------
//  DIÁLOGO PARA CREAR UN NUEVO EVENTO
// ----------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCalendarItemDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (CalendarItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(CalendarItemType.ESTUDIO) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    var isTitleError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ButtonPrimary.copy(alpha = 0.97f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    "Nuevo evento",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; isTitleError = false },
                    label = { Text("Título", fontSize = 15.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isTitleError,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)", fontSize = 15.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LinkGreen,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = LinkGreen,
                        focusedLabelColor = LinkGreen,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha (por ahora solo se muestra; si quieres luego integramos DatePicker)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val formatter =
                        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))
                    Text(
                        "Fecha: ${selectedDate.format(formatter)}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Tipo de evento",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalendarTypeChip(
                        label = "Estudio",
                        isSelected = type == CalendarItemType.ESTUDIO,
                        color = ButtonPrimary
                    ) { type = CalendarItemType.ESTUDIO }

                    CalendarTypeChip(
                        label = "Hábito",
                        isSelected = type == CalendarItemType.HABITO,
                        color = LinkGreen
                    ) { type = CalendarItemType.HABITO }

                    CalendarTypeChip(
                        label = "Personal",
                        isSelected = type == CalendarItemType.PERSONAL,
                        color = Color(0xFFFFD54F)
                    ) { type = CalendarItemType.PERSONAL }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = LinkGreen, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                isTitleError = true
                            } else {
                                onSave(
                                    CalendarItem(
                                        date = selectedDate,
                                        title = title,
                                        description = description,
                                        type = type
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LinkGreen)
                    ) {
                        Text("Guardar", color = Color.Black, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarTypeChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) color.copy(alpha = 0.18f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = color,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}


