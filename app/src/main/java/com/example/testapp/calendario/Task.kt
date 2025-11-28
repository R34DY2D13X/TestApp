package com.example.testapp.calendario

import java.time.LocalDate

enum class TaskType { ESTUDIO, HABITO, PERSONAL }

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = LocalDate.now().toString(),
    val type: TaskType = TaskType.PERSONAL,
    val isCompleted: Boolean = false,
    val userId: String = ""
)