package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.wellbee.frontend.screens.WelcomeScreen
import com.example.wellbee.frontend.screens.RegisterScreen
import com.example.wellbee.frontend.screens.LoginScreen
import com.example.wellbee.frontend.screens.ResetPasswordScreen
import com.example.wellbee.frontend.screens.HomeScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Mental.MentalHealthScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("reset_password") { ResetPasswordScreen(navController) }

        // Bottom Nav Pages
        composable("home") { HomeScreen(navController) }
        composable("physical") { PhysicalHealthScreen() }
        composable("mental") { MentalHealthScreen() }
        composable("education") { EducationScreen() }
    }
}


