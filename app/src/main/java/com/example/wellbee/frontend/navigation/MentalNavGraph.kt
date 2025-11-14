package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.screens.mental.DiaryScreen
import com.example.wellbee.frontend.screens.mental.MentalHealthScreen
import com.example.wellbee.frontend.screens.Mental.JournalListScreen
import com.example.wellbee.frontend.screens.mental.DetailDiaryScreen

@Composable
fun MentalNavGraph(parentNavController: NavHostController) {

    val mentalNavController = rememberNavController()

    NavHost(
        navController = mentalNavController,
        startDestination = "mental_home"
    ) {

        composable("mental_home") {
            MentalHealthScreen(navController = mentalNavController)
        }

        composable("diary") {
            DiaryScreen(navController = mentalNavController)
        }

        composable("journal_list") {
            JournalListScreen(navController = mentalNavController)
        }

        composable("detail_diary/{title}") { backStackEntry ->

            val title = backStackEntry.arguments?.getString("title") ?: ""

            DetailDiaryScreen(
                navController = mentalNavController,
                date = "Nov, 15 2025",
                title = title,
                content = "Isi diary akan dimuat di sini..."
            )
        }
    }
}
