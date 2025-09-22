package com.example.test_chat_bot

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi @Serializable
data class OpenAIRequest(val model: String, val messages: List<OpenAIMessage>)

@InternalSerializationApi @Serializable
data class OpenAIMessage(val role: String, val content: String)

@InternalSerializationApi @Serializable
data class hugginFace(val prompt: String)

@InternalSerializationApi @Serializable
data class OpenAIResponse(val choices: List<Choice> = emptyList()){
    @Serializable
    data class Choice(
        val index: Int,
        val message: OpenAIMessage? = null,
        val finish_reason: String? = null
    )
}