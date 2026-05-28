package com.example.formfit.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.R
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.formfit.ui.theme.OswaldFontFamily

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

    val assetUri = if (exercise.assetResId == null) {
        "android.resource://${context.packageName}/raw/${exercise.assetName}"
    } else {""}

    val exoPlayer = remember(exerciseId) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(assetUri))
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(exerciseId) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = Modifier.border(
            width = 2.dp,
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
        ).padding(10.dp)

    ) {
        Text(
            exercise.name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp),
            fontFamily = OswaldFontFamily
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
            modifier = Modifier.padding(4.dp),
            fontFamily = OswaldFontFamily
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
            modifier = Modifier.padding(4.dp),
            fontFamily = OswaldFontFamily
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
            modifier = Modifier.padding(4.dp),
            fontFamily = OswaldFontFamily
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (exercise.assetResId != null) {
            Image(
                painter = painterResource(exercise.assetResId),
                contentDescription = exercise.name,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                update = { playerView ->
                    playerView.player = exoPlayer
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.White)
        ) {
            Text(
                text = "Check Your Form",
                color = Color.White,
                fontFamily = OswaldFontFamily)
        }
    }

}