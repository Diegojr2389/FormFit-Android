package com.example.formfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.formfit.ui.components.MainTabs
import com.example.formfit.ui.theme.FormFitTheme

@androidx.camera.core.ExperimentalGetImage
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormFitTheme {
                MainTabs(
                    navController = rememberNavController()
                )
            }
        }
    }
}

@androidx.camera.core.ExperimentalGetImage
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FormFitTheme {
        MainTabs(
            navController = rememberNavController()
        )
    }
}