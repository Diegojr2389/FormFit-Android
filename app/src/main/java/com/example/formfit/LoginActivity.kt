package com.example.formfit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.formfit.ui.screens.LoginScreen
import com.example.formfit.ui.theme.FormFitTheme

@androidx.camera.core.ExperimentalGetImage
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormFitTheme {
                LoginScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // user is logged in, no reason to go back to login
                    },
                    onNavigateToSignUp = {
                        startActivity(Intent(this, SignUpActivity::class.java))
                        // no finish() here — let them come back if they want
                    }
                )
            }
        }
    }
}