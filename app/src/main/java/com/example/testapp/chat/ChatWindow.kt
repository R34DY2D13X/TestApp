package com.example.testapp.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatWindow(
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit,
    messages: List<Pair<String, Boolean>> // true = usuario, false = IA
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClose() } // Close when clicking outside
            .background(Color.Black.copy(alpha = 0.6f)) // Darker, semi-transparent background
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C3E50) // A slightly more neutral dark blue
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Tu asistente de hábitos 😊",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Chat History
                Column(
                    modifier = Modifier
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    messages.forEach { (msg, isUser) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Text(
                                text = msg,
                                modifier = Modifier
                                    .background(
                                        if (isUser) Color(0xFF4A90E2) else Color(0xFF3B4A61), // Brighter blue for user, subtle for AI
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // User Input
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = message,
                        onValueChange = onMessageChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe aquí…", color = Color.White.copy(alpha = 0.7f)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedContainerColor = Color(0xFF3B4A61),
                            unfocusedContainerColor = Color(0xFF3B4A61),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.width(10.dp))

                    Button(
                        onClick = onSend,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text("Enviar", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Cerrar",
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onClose() }
                        .padding(8.dp)
                )
            }
        }
    }
}