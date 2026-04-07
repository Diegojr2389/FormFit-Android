package com.example.formfit.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.formfit.data.local.EXERCISES
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ExerciseCard(exerciseId: String?, navController: NavController) {
    if (exerciseId.isNullOrBlank()) return
    val exercise = EXERCISES.find {it.id == exerciseId}
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasMicPermission by remember { mutableStateOf(false) }
    var requested by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
            val micGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

            requested = true

            if (cameraGranted && micGranted) {
                // Permission granted → navigate to CameraScreen
                navController.navigate("camera/$exerciseId")
            }
            hasCameraPermission = cameraGranted
            hasMicPermission = micGranted
        }
    )

    if (exercise == null) {
        Text("Exercise Not Found")
        return
    }
    Column(
        modifier = Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp),
        ).padding(10.dp)

    ) {
        Text(
            exercise.name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 18.sp)) {
                    append("Primary Muscle(s): ")
                }
                withStyle(style = SpanStyle(color = Color.White)) {
                    append(exercise.primaryMuscle.joinToString(", "))
                }
            },
            modifier = Modifier.padding(4.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 18.sp)) {
                    append("Secondary Muscle(s): ")
                }
                withStyle(style = SpanStyle(color = Color.White)) {
                    append(exercise.secondaryMuscle.joinToString(", "))
                }
            },
            modifier = Modifier.padding(4.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, fontSize = 18.sp)) {
                    append("Description: ")
                }
                withStyle(style = SpanStyle(color = Color.White)) {
                    append(exercise.description)
                }
            },
            modifier = Modifier.padding(4.dp)
        )

        if (requested && !hasCameraPermission) Text("Camera Permission Denied", color = Color.Red)
        if (requested && !hasMicPermission) Text("Mic Permission Denied", color = Color.Red)
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                        )
                    )
                }
                else navController.navigate("camera/$exerciseId")
            },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        ) {
            Text("Check Your Form", color = Color.White)
        }
    }

}