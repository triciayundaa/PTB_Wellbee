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
        // Halaman utama modul fisik (dashboard)
//        composable("dashboard") {
//            androidx.compose.foundation.layout.Column {
//                PhysicalHealthHeader(navController, currentTab = "None")
//                PhysicalDashboardContent()
//            }
//        }

        // 🟩 Dashboard
        composable("dashboard") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "None")
                PhysicalDashboardContent()
            }
        }

        // 🟨 Sport
        composable("sport_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "Sport")
                SportScreen(navController)
            }
        }

        // 🟦 Sleep
        composable("sleep_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "Sleep")
                SleepScreen(navController = navController)
            }
        }

        // 🟧 Weight
        composable("weight_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "BeratBadan")
                WeightScreen(navController = navController)
            }
        }

            // 🟥 Riwayat
            composable("riwayat_screen") {
                RiwayatScreen(navController = navController)
            }

            // Halaman Sport
//        composable("sport_screen") {
//            androidx.compose.foundation.layout.Column {
//                PhysicalHealthHeader(navController, currentTab = "Sport")
//                SportScreen(navController)
//            }
//        }
//
//        composable("sleep_screen") {
//            PhysicalHealthHeader(navController, currentTab = "Sleep")
//            SleepScreen(navController = navController)
//        }
//        composable("weight_screen") {
//            PhysicalHealthHeader(navController, currentTab = "BeratBadan")
//            WeightScreen(navController = navController)
//        }

            // Jika nanti mau aktifkan Sleep & Weight:
            /*
        composable("sleep_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "Sleep")
                SleepScreen(navController)
            }
        }

        composable("weight_screen") {
            Column {
                PhysicalHealthHeader(navController, currentTab = "BeratBadan")
                WeightScreen(navController)
            }
        }
        */
        }
    }
