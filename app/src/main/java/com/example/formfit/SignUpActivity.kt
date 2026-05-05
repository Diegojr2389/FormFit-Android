package com.example.formfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.formfit.ui.screens.SignUpScreen
import com.example.formfit.ui.theme.FormFitTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            // navigation bar color
            navigationBarStyle = SystemBarStyle.dark(0xFF111111.toInt())
        )

        setContent {
            FormFitTheme {
                SignUpScreen(
                    onSignUpSuccess = { finish() }, // Return to login after successful sign up
                    onNavigateToLogin = { finish() } // Finish activity and return to LoginActivity
                )
            }
        }
    }
}