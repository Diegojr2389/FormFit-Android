package com.example.formfit.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.formfit.data.local.EXERCISES
import com.example.formfit.ui.components.ExerciseCard
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun ExercisesScreen(navController : NavController) {
    // keeps track of what exercise we are looking at
    var selectedId by remember { mutableStateOf<String?>(null) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        color = MaterialTheme.colorScheme.onBackground
    ) {
        Column {
            Text("Exercises", color = Color.White, fontSize = 30.sp, modifier = Modifier.padding(10.dp))

            // FlowRow: Arranges UI elements horizontally and wraps them to next line automatically
            FlowRow(
                mainAxisSpacing = 12.dp,
                crossAxisSpacing = 2.dp,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                EXERCISES.forEach { exercise ->
                    val isSelected = selectedId == exercise.id
                    Button(
                        onClick = {selectedId = exercise.id}, // updates selectedId
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    )
                    {
                        Text(
                            exercise.name,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else Color.LightGray
                        )
                    }
                }
            }

            // Displays Exercise Card corresponding to the button that was clicked
            ExerciseCard(exerciseId = selectedId, navController)
        }
    }
}