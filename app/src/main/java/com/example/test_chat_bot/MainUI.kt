package com.example.test_chat_bot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.os.BuildCompat
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

class MainUI: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatScreen()
        }
    }
}

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val apiKey = ""
    val openAI = OpenAIService(apiKey)

    var messages by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                Text("${msg.first}: ${msg.second}", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
            }
        }

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Введите сообщение") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (input.isNotBlank()) {
                    val userMsg = input
                    messages = messages + ("Ты" to userMsg)
                    input = ""

                    scope.launch {
                        val reply = openAI.getResponse(userMsg)
                        messages = messages + ("Бот" to reply)
                    }
                }
            }) {
                Text("Отправить")
            }
        }
    }
}
