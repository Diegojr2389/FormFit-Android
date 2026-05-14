package com.example.formfit.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UserData

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val user by UserDataStore
        .getUser(context)
        .collectAsState(
            initial = UserData(
                null,
                null,
                null,
                null))

    val userId = user.userId ?: -1
    val username = user.username ?: ""

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Text(
            text="Hello User: $userId, Username: $username",
            fontSize = 32.sp,
            color = Color.White
        )
    }
}