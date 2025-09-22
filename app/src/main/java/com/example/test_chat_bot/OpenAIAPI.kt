package com.example.test_chat_bot

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenAIService(private val apiKey: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun getResponse(userMessage: String): String = withContext(Dispatchers.IO) {
        try {


            val httpResponse = client.post("https://router.huggingface.co/v1") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                    append(HttpHeaders.ContentType, Json.toString())
                }
                setBody(
                    OpenAIRequest(
                        model = "Qwen/Qwen3-Next-80B-A3B-Thinking:together",
                        messages = listOf(OpenAIMessage("user", userMessage))
                    )
                )
            }

            val rawResponse = httpResponse.bodyAsText()
            println("RAW RESPONSE: $rawResponse")

            val json = Json { ignoreUnknownKeys = true }

            val errorMessage = try {
                val errorJson = json.parseToJsonElement(rawResponse).jsonObject
                errorJson["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content
            } catch (_: Exception) { null }

            if (errorMessage != null) return@withContext "Ошибка API: $errorMessage"

            val response = json.decodeFromString(OpenAIResponse.serializer(), rawResponse)
            response.choices.firstOrNull()?.message?.content ?: "Ошибка: пустой ответ"

        } catch (e: Exception) {
            "Ошибка запроса: ${e.message}"
        }
    }
}
