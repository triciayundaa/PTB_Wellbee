package com.example.wellbee.frontend.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PhysicalHealthHeader(
    navController: NavController,
    currentTab: String
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0E4DA4))
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Physical Health",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Riwayat",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        navController.navigate("riwayat_screen") {
                            launchSingleTop = true
                        }
                    }
                )

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0E4DA4))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabButton(
                label = "Sport",
                icon = Icons.Default.FitnessCenter,
                selected = currentTab == "Sport",
                onClick = {
                    if (currentTab != "Sport") {
                        navController.navigate("sport_screen") {
                            launchSingleTop = true
                        }
                    }
                }

            )
            TabButton(
                label = "Sleep",
                icon = Icons.Default.Nightlight,
                selected = currentTab == "Sleep",
                onClick = {
                    if (currentTab != "Sleep") {
                        navController.navigate("sleep_screen") {
                            launchSingleTop = true
                        }
                    }
                }
            )
            TabButton(
                label = "Berat Badan",
                icon = Icons.Default.MonitorWeight,
                selected = currentTab == "BeratBadan",
                onClick = {
                    if (currentTab != "BeratBadan") {
                        navController.navigate("weight_screen") {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color(0xFF00B894) else Color(0xFF0E4DA4))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
