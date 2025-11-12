package com.example.wellbee.frontend.screens.Fisik

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RiwayatScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
        // ===== Header =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Riwayat",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E4DA4)
            )

            // Tombol Date dropdown (sementara dummy)
            Button(
                onClick = { /* nanti bisa pilih tanggal */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Date", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Tab kategori =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0E4DA4), RoundedCornerShape(50.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Sport", "Sleep", "Berat Badan")
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isSelected) Color(0xFF00B894) else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = tab,
                        color = Color.White,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ===== Konten berdasarkan tab =====
        when (selectedTab) {
            null -> PilihKategoriScreen()
            "Sport" -> SportRiwayatList()
            "Sleep" -> SleepRiwayatList()
            "Berat Badan" -> BeratBadanRiwayatList()
        }

    }
}
@Composable
fun PilihKategoriScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pilih kategori untuk melihat riwayat",
            color = Color.Gray
        )
    }
}

// =============================
// ======= Sport Section =======
// =============================
@Composable
fun SportRiwayatList() {
    val riwayat = listOf(
        "Lari" to "30 menit",
        "Gym" to "45 menit"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        riwayat.forEach { (nama, durasi) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = nama, fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                        Text(text = durasi, color = Color(0xFF00B894))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { /* TODO edit */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00B894))
                        }
                        IconButton(onClick = { /* TODO hapus */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFD63031))
                        }
                    }
                }
            }
        }
    }
}

// =============================
// ======= Sleep Section =======
// =============================
@Composable
fun SleepRiwayatList() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Belum ada data tidur", color = Color.Gray)
    }
}


// =============================
// ==== Berat Badan Section ====
// =============================

@Composable
fun BeratBadanRiwayatList() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Belum ada data berat badan", color = Color.Gray)
    }
}
