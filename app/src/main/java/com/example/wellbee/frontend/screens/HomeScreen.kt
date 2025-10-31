package com.example.wellbee.frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.navigation.BottomNavItem
import com.example.wellbee.frontend.screens.DashboardContent
import com.example.wellbee.frontend.screens.Fisik.PhysicalHealthScreen
import com.example.wellbee.frontend.screens.Mental.MentalHealthScreen
import com.example.wellbee.frontend.screens.Edukasi.EducationScreen



@Composable
fun HomeScreen(parentNavController: NavHostController) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomNavItem("home_tab", "Home", Icons.Default.Home),
        BottomNavItem("physical_tab", "Physical", Icons.Default.FitnessCenter),
        BottomNavItem("mental_tab", "Mental", Icons.Default.Favorite),
        BottomNavItem("education_tab", "Education", Icons.Default.MenuBook)
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = bottomItems)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "home_tab"
            ) {
                composable("home_tab") { DashboardContent() }
                composable("physical_tab") { PhysicalHealthScreen() }
                composable("mental_tab") { MentalHealthScreen() }
                composable("education_tab") { EducationScreen() }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}