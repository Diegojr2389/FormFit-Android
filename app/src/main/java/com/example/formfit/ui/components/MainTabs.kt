package com.example.formfit.ui.components

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.formfit.LoginActivity
import com.example.formfit.R
import com.example.formfit.ui.screens.CameraScreen
import com.example.formfit.ui.screens.ChatbotScreen
import com.example.formfit.ui.screens.EditProfileScreen
import com.example.formfit.ui.screens.ExercisesScreen
import com.example.formfit.ui.screens.HomeScreen
import com.example.formfit.ui.screens.ProfileScreen
import com.example.formfit.viewmodel.ProfileViewModel

@androidx.camera.core.ExperimentalGetImage
@OptIn(ExperimentalLayoutApi::class)
@Composable // allows rendering in the UI
fun MainTabs(navController: NavHostController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            if (currentRoute != "chatbot" && currentRoute != "edit_profile") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    tonalElevation = 0.dp
                ){
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (currentRoute == "home") R.drawable.ic_home_filled
                                    else R.drawable.ic_home
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "exercises",
                        onClick = { navController.navigate("exercises") },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (currentRoute == "exercises") R.drawable.ic_dumbbell_filled
                                    else R.drawable.ic_dumbbell
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "chatbot",
                        onClick = { navController.navigate("chatbot") },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (currentRoute == "chatbot") R.drawable.ic_chat_filled
                                    else R.drawable.ic_chat
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") },
                        icon = {
                            Icon(
                                painter = painterResource(
                                    id = if (currentRoute == "profile") R.drawable.ic_profile_filled
                                    else R.drawable.ic_profile
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            modifier = Modifier.padding(
                PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = if (currentRoute == "chatbot") 0.dp else innerPadding.calculateBottomPadding()
                )
            )
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
            composable("chatbot") { ChatbotScreen(navController) }
            composable("profile")  {
                val context = LocalContext.current
                ProfileScreen(
                    onLoggedOut = {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        // safely casts the context into an Activity so that we can call finish()
                        (context as? ComponentActivity)?.finish()
                    },
                    navController
                )
            }
            composable("edit_profile") { EditProfileScreen(navController) }
        }
    }
}