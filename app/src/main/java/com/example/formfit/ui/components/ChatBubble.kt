package com.example.formfit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.formfit.models.ChatMessage
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    var displayedText by remember(message.message) { mutableStateOf("") }

    LaunchedEffect(message.message) {
        if (!isUser && message.isNew) {
            message.isNew = false
            displayedText = ""
            message.message.forEachIndexed { index, _ ->
                delay(20)
                displayedText = message.message.take(index + 1)
            }
        }
        else {
            displayedText = message.message
            message.isNew = false
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary else Color(0xFF1E1E1E),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = displayedText,
                    color = Color.White
                )
                Text(
                    text = message.createdAt,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}