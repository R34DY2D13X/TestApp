package com.example.testapp.calendario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testapp.ajustes.SettingsViewModel
import com.example.testapp.ui.theme.ButtonPrimary
import com.example.testapp.ui.theme.GradientEnd
import com.example.testapp.ui.theme.LinkGreen
import com.example.testapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class CalendarItemType { ESTUDIO, HABITO, PERSONAL }

data class CalendarItem(
    val date: LocalDate,
    val title: String,
    val description: String,
    val type: CalendarItemType
)

@Composable
fun CalendarioScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {

    val settings by settingsViewModel.uiState.collectAsState()
    val highContrast = settings.highContrast

    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val calendarItems = remember { mutableStateListOf<CalendarItem>() }
    var showAddDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val dynamicBg = if (highContrast) Color.Black else GradientEnd
    val dynamicPrimaryText = if (highContrast) Color.White else Color.White
    val dynamicSecondaryText = if (highContrast) Color.LightGray else TextSecondary
    val dynamicSelectedBg = if (highContrast) LinkGreen else ButtonPrimary.copy(alpha = 0.7f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBg)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowLeft, "Mes anterior", tint = dynamicPrimaryText)
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es")))
                    .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                color = dynamicPrimaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.KeyboardArrowRight, "Mes siguiente", tint = dynamicPrimaryText)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val daysOfWeek = DayOfWeek.values().map { it.getDisplayName(TextStyle.SHORT, Locale("es")) }
            daysOfWeek.forEach { day ->
                Text(
                    text = day.uppercase(),
                    color = dynamicSecondaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val firstDayOfMonth = currentMonth.withDayOfMonth(1)
        val lastDayOfMonth = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val totalDays = 42
        val dates = (1..totalDays).map {
            val dayOffset = it - firstDayOfWeek - 1
            firstDayOfMonth.plusDays(dayOffset.toLong())
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(dates) { date ->
                val day = date.dayOfMonth
                val isSelected = date == selectedDate
                val itemsOfDay = calendarItems.filter { it.date == date }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedDate = date }
                        .background(
                            if (isSelected) dynamicSelectedBg
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
                            color = dynamicPrimaryText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 17.sp
                        )

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
                color = dynamicPrimaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )

            if (itemsSelectedDay.isEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No tienes nada registrado para este día.\nToca el botón + para agregar algo.",
                    color = dynamicSecondaryText,
                    fontSize = 14.sp
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(itemsSelectedDay) { item ->
                        CalendarItemRow(item = item, highContrast = highContrast)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

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

@Composable
private fun CalendarItemRow(item: CalendarItem, highContrast: Boolean) {
    val chipColor = when (item.type) {
        CalendarItemType.ESTUDIO -> ButtonPrimary
        CalendarItemType.HABITO -> LinkGreen
        CalendarItemType.PERSONAL -> Color(0xFFFFD54F)
    }
    val dynamicCardBg = if (highContrast) Color(0xFF2E2E2E) else ButtonPrimary.copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(dynamicCardBg)
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))
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