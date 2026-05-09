package com.example.formfit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.formfit.R
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.ui.components.ChatBubble
import com.example.formfit.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatbotScreen(
    navController: NavHostController,
    viewModel: ChatViewModel = viewModel(
        factory = ChatViewModel.Factory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    // Tracks the press interaction state of the button (e.g. pressed, released)
    val interactionSource = remember { MutableInteractionSource() }
    // Returns true when the button is currently being pressed, false otherwise
    val isPressed by interactionSource.collectIsPressedAsState()
    // Message typed in by user
    var message by remember { mutableStateOf("") }

    val outputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())

    val user by UserDataStore.getUser(context).collectAsState(initial = Pair(null, null))

    val userId = user.first

    // only enable send when userId is loaded
    val isSendEnabled = userId != null && message.isNotBlank()

    val messages = viewModel.messages

    val listState = rememberLazyListState()

    // show the send button only when user has started typing a message
    val trailingIcon: @Composable (() -> Unit)? = if (message.isNotBlank()) {
        {
            IconButton(
                onClick = {
                    userId?.let {
                        viewModel.send(userId, message, outputFormatter.format(Date()))
                        message = ""
                    }
                },
                enabled = isSendEnabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send_filled),
                    contentDescription = "send",
                    tint = Color.White
                )
            }
        }
    } else null

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 5.dp, bottom = 5.dp, end = 5.dp)
        ,
        color = MaterialTheme.colorScheme.onBackground,

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onBackground)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate("home")},
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isPressed) Color(0xFF3C475A) else Color.Transparent,
                                shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_forma),
                        contentDescription = "Forma",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .padding(start = 0.dp, top = 0.dp, end = 10.dp, bottom = 0.dp)
                    )
                    Column {
                        Text(
                            text = "Forma",
                            color = Color.White
                        )
                        Text(
                            text = "Online",
                            fontSize = 12.sp,
                            color = Color(0xFF00C853)
                        )
                    }
                }

                HorizontalDivider(
                    color = Color(0xFF5A6882),
                    thickness = 1.dp
                )
            }

            // Messages scroll behind the header
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                contentPadding = PaddingValues(10.dp)
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(message)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            TextField(
                value = message,
                onValueChange = { message = it},
                label = { Text("Message...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                ),
                trailingIcon = trailingIcon
            )

        }
    }
}