package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wellbee.data.SessionManager
import com.example.wellbee.frontend.screens.WelcomeScreen
import com.example.wellbee.frontend.screens.RegisterScreen
import com.example.wellbee.frontend.screens.LoginScreen
import com.example.wellbee.frontend.screens.ResetPasswordScreen
import com.example.wellbee.frontend.screens.MainScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Tentukan startDestination secara dinamis
    val startDest = if (sessionManager.isLoggedIn()) "main" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        // 🔹 AUTH SCREENS
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("reset_password") { ResetPasswordScreen(navController) }

        // 🔹 MAIN SCREEN
        composable("main") { MainScreen(parentNavController = navController) }
    }
}