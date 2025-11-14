package com.example.wellbee.frontend.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.components.BottomNavigationBar
import com.example.wellbee.frontend.navigation.MentalNavGraph
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen

@Composable
fun MainScreen(parentNavController: NavHostController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen() }
                composable("education") { EducationScreen() }
                composable("mental") {
                    MentalNavGraph(parentNavController = navController)
                }
                composable("physical") { PhysicalHealthScreen() }
            }
        }
    }
}
