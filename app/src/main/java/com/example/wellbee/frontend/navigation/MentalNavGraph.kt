package com.example.wellbee.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wellbee.frontend.screens.Mental.DiaryScreen
import com.example.wellbee.frontend.screens.Mental.MentalHealthScreen
import com.example.wellbee.frontend.screens.Mental.JournalListScreen
import com.example.wellbee.frontend.screens.Mental.DetailDiaryScreen


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

        // Updated route to accept ID
        composable(
            route = "detail_diary/{journalId}",
            arguments = listOf(navArgument("journalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val journalId = backStackEntry.arguments?.getInt("journalId") ?: 0
            DetailDiaryScreen(
                navController = mentalNavController,
                journalId = journalId
            )
        }
    }
}
