package com.example.wellbee.frontend.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wellbee.ui.theme.BluePrimary
import com.example.wellbee.ui.theme.GreenAccent
import com.example.wellbee.ui.theme.GrayBackground

@Composable
fun DashboardContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(16.dp)
        ) {
            Text("Weelbee", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("10222", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Langkah", color = Color.Gray)
                }
            }

            Card(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("60 kg", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("BMI: 19.5", color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenAccent.copy(alpha = 0.15f))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("8 Jam Tidur", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Kualitas: ⭐⭐⭐⭐☆", color = Color.DarkGray)
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Artikel Terbaru", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
        Card(Modifier.padding(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Tips Tidur Nyenyak dan Nyaman", fontWeight = FontWeight.Bold)
                Text("Kesehatan Fisik • 5 menit", color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text("Baca Selengkapnya →", color = BluePrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

