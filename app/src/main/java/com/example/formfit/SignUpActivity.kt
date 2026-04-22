package com.example.formfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.formfit.ui.screens.SignUpScreen
import com.example.formfit.ui.theme.FormFitTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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