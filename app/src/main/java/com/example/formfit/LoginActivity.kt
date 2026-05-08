package com.example.formfit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.formfit.datastore.SessionManager
import com.example.formfit.ui.screens.LoginScreen
import com.example.formfit.ui.theme.FormFitTheme
import kotlinx.coroutines.launch

@androidx.camera.core.ExperimentalGetImage
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(applicationContext)

        lifecycleScope.launch {
            val isLoggedIn = sessionManager.isLoggedIn()

            if (isLoggedIn) {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )
                )

                finish()
            }
            else {
                showLoginScreen()
            }
        }
    }

    private fun showLoginScreen() {
        enableEdgeToEdge(
            // navigation bar color
            navigationBarStyle = SystemBarStyle.dark(0xFF111111.toInt())
        )

        setContent {
            FormFitTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // intent = messaging object used to start another activity
                        // can carry extra data like "username" to the destination activity
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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