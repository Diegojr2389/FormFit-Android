package com.example.formfit.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.formfit.ui.screens.CameraScreen
import com.example.formfit.ui.screens.ChatbotScreen
import com.example.formfit.ui.screens.ExercisesScreen
import com.example.formfit.ui.screens.HomeScreen
import com.example.formfit.ui.screens.ProfileScreen

@androidx.camera.core.ExperimentalGetImage
@OptIn(ExperimentalLayoutApi::class)
@Composable // allows rendering in the UI
fun MainTabs(navController: NavHostController) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.onBackground,
                tonalElevation = 0.dp
            ){
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(36.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "exercises",
                    onClick = { navController.navigate("exercises") },
                    icon = { Icon(Icons.Filled.FitnessCenter, null, modifier = Modifier.size(36.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "chatbot",
                    onClick = { navController.navigate("chatbot") },
                    icon = { Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(40.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(36.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("exercises") { ExercisesScreen(navController) }
            composable(
                "camera/{exerciseId}",
                // declare expected navigation arguments
                arguments = listOf(navArgument("exerciseId") {
                    // Specify that the argument type must be a String
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                // return value passed in the route
                val exerciseId =
                    backStackEntry.arguments?.getString("exerciseId")
                // Pass the retrieved exerciseId into CameraScreen
                // so it can adjust its pose detection logic accordingly
                CameraScreen(exerciseId = exerciseId)
            }
            composable("chatbot") { ChatbotScreen() }
            composable("profile")  { ProfileScreen() }
        }
    }
}