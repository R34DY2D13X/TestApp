package com.example.testapp.chat

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatUIScreen(viewModel: ChatbotViewModel = viewModel()) {
    var showChat by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // The button is visible only when the chat is closed.
        AnimatedVisibility(
            visible = !showChat,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ChatbotButton(onClick = { showChat = true })
        }

        // The chat window animates in from the bottom.
        AnimatedVisibility(
            visible = showChat,
            enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
            exit = slideOutVertically { fullHeight -> fullHeight } + fadeOut()
        ) {
            ChatWindow(
                message = message,
                onMessageChange = { message = it },
                onSend = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(message)
                        message = ""
                    }
                },
                onClose = { showChat = false },
                messages = viewModel.messages
            )
        }
    }
}