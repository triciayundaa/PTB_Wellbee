package com.example.wellbee.frontend.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.components.BottomNavigationBar
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.mental.MentalHealthScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen

@Suppress("UNUSED_PARAMETER")
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
                composable("mental") { MentalHealthScreen(navController = navController) }
                composable("physical") { PhysicalHealthScreen() }

                composable("diary") {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text("Diary Screen")
                    }
                }
            }
        }
    }
}
