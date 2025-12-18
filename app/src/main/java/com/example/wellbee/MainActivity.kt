package com.example.wellbee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.navigation.NavGraph
import com.example.wellbee.ui.theme.WellbeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WellbeeTheme {
                val navController = rememberNavController()
                // NavGraph sekarang secara otomatis menangani rute awal (Login atau Main)
                NavGraph(navController = navController)
            }
        }
    }
}