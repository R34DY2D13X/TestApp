package com.example.testapp.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val generativeModel = GenerativeModel(
        // Using the correct, modern Gemini model as per the user's latest research.
        modelName = "gemini-1.5-flash-latest",
        apiKey = apiKey
    )

    var messages = mutableStateListOf<Pair<String, Boolean>>() 
        private set

    fun sendMessage(userMsg: String) {
        messages.add(userMsg to true)

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(userMsg)
                val reply = response.text ?: "Lo siento, no entendí 😅"
                messages.add(reply to false)
            } catch (e: Exception) {
                // Show the raw, technical error for definitive debugging.
                messages.add("Error: ${e.message}" to false)
            }
        }
    }
}