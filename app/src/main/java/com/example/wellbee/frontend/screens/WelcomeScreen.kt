package com.example.wellbee.frontend.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.ui.theme.WellbeeTheme

@Composable
fun WelcomeScreen(navController: NavController) {
    // Definisi warna manual agar konsisten
    val BluePrimary = Color(0xFF0E4DA4)
    val White = Color.White
    val AccentGreen = Color(0xFF00B894) // Warna aksen untuk tombol kedua

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePrimary) // 🔥 Latar TETAP BIRU
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Judul di tengah layar
            Text(
                text = "Welcome at Wellbee",
                color = White, // 🔥 Tulisan jadi PUTIH
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Tombol di bagian bawah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tombol Register (Putih agar kontras di latar biru)
                Button(
                    onClick = { navController.navigate("register") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = BluePrimary
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        text = "Register",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Tombol Login/Start (Warna Aksen agar beda)
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentGreen,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        text = "Lets Get Start",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WellbeeTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController)
    }
}