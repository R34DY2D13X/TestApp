package com.example.testapp.chat

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService(apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun sendMessage(message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(
                            """
                            Eres un asistente integrado dentro de la app HabiCut.
                            Responde de forma amable, breve y útil. 
                            Si el usuario está triste, anímalo.
                            Si el usuario pide hábitos o salud, da recomendaciones claras.
                            
                            Usuario: $message
                            """.trimIndent()
                        )
                    }
                )
                response.text ?: "No tengo una respuesta ahora mismo."
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }
}