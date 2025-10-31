package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen

import com.example.wellbee.frontend.screens.WelcomeScreen
import com.example.wellbee.frontend.screens.RegisterScreen
import com.example.wellbee.frontend.screens.LoginScreen
import com.example.wellbee.frontend.screens.ResetPasswordScreen
import com.example.wellbee.frontend.screens.MainScreen // <--- MainScreen sekarang jadi halaman utama

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // Halaman Awal & Auth
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("reset_password") { ResetPasswordScreen(navController) }

        // Halaman utama setelah login (berisi bottom nav)
        composable("main") { MainScreen(navController) }

        composable("education") {
            EducationScreen(navController = navController)
        }

    }
}
