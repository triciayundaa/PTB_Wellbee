package com.example.wellbee.frontend.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wellbee.frontend.components.PhysicalHealthHeader
import com.example.wellbee.frontend.screens.Fisik.PhysicalDashboardContent
import com.example.wellbee.frontend.screens.Fisik.RiwayatScreen
import com.example.wellbee.frontend.screens.Fisik.SleepScreen
import com.example.wellbee.frontend.screens.Fisik.SportScreen
import com.example.wellbee.frontend.screens.Fisik.WeightScreen

@Composable
fun PhysicalNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "None")
                PhysicalDashboardContent()
            }
        }


        composable("sport_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "Sport")
                SportScreen(navController)
            }
        }


        composable("sleep_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "Sleep")
                SleepScreen(navController = navController)
            }
        }


        composable("weight_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "BeratBadan")
                WeightScreen(navController = navController)
            }
        }


        composable("riwayat_screen") {
            RiwayatScreen(navController = navController)
        }

    }
}