package com.example.wellbee.frontend.screens

import android.annotation.SuppressLint
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
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Mental.MentalHealthScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen
import com.example.wellbee.frontend.screens.Fisik.SportScreen
import com.example.wellbee.frontend.screens.Home.HomeScreen

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun MainScreen(parentNavController: NavHostController) {
    val navController = rememberNavController() // khusus untuk bottom nav di dalam main screen

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
                composable("mental") { MentalHealthScreen() }
                composable("physical") { PhysicalHealthScreen(navController) }

                    // 🔥 Tambahkan rute halaman modul fisik
//                    composable("sleep_screen") { SleepScreen(navController) }
//                    composable("weight_screen") { WeightScreen(navController) }
                }
            }
        }
    }
