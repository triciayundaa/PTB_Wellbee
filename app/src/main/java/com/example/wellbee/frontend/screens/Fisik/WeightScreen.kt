@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.wellbee.frontend.screens.Fisik

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.frontend.components.showDatePicker

@Composable
fun WeightScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val activity = LocalActivity.current  // ✅ Wajib diambil di luar lambda

    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // 🔢 Hitung BMI otomatis
    LaunchedEffect(beratBadan, tinggiBadan) {
        val berat = beratBadan.toDoubleOrNull()
        val tinggi = tinggiBadan.toDoubleOrNull()
        if (berat != null && tinggi != null && tinggi > 0) {
            val bmiValue = berat / ((tinggi / 100) * (tinggi / 100))
            bmi = String.format("%.1f", bmiValue)
            kategori = when {
                bmiValue < 18.5 -> "Kurus"
                bmiValue < 25 -> "Normal"
                bmiValue < 30 -> "Kelebihan Berat"
                else -> "Obesitas"
            }
        } else {
            bmi = ""
            kategori = ""
        }
    }

    // 🧱 UI utama
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
        Text(
            text = "Catat Berat & Tinggi Badan Kamu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ⚖️ Berat badan
        OutlinedTextField(
            value = beratBadan,
            onValueChange = { beratBadan = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Berat Badan (kg)") },
            placeholder = { Text("Contoh: 68") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 📏 Tinggi badan
        OutlinedTextField(
            value = tinggiBadan,
            onValueChange = { tinggiBadan = it.filter { c -> c.isDigit() || c == '.' } },
            label = { Text("Tinggi Badan (cm)") },
            placeholder = { Text("Contoh: 170") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 📅 Pilih tanggal
        OutlinedTextField(
            value = tanggal,
            onValueChange = {},
            label = { Text("Tanggal Pengukuran") },
            placeholder = { Text("Pilih tanggal") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    activity?.let {
                        showDatePicker(
                            context = it,
                            onDateSelected = { pickedDate ->
                                tanggal = pickedDate
                            }
                        )
                    }
                },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📊 Kartu BMI
        if (bmi.isNotEmpty()) {
            val cardColor = when (kategori) {
                "Kurus" -> Color(0xFFD6EAF8)
                "Normal" -> Color(0xFFDFFFE3)
                "Kelebihan Berat" -> Color(0xFFFFF7D6)
                "Obesitas" -> Color(0xFFFFD6D6)
                else -> Color.White
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BMI Anda", color = Color(0xFF0E4DA4), fontWeight = FontWeight.Bold)
                    Text(bmi, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(kategori, fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Kurus < 18.5")
                        Text("Normal 18.5 - 24.9")
                        Text("Kelebihan Berat 25 - 29.9")
                        Text("Obesitas ≥ 30")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Tombol aksi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    beratBadan = ""
                    tinggiBadan = ""
                    tanggal = ""
                    bmi = ""
                    kategori = ""
                    navController.navigate("dashboard") {
                        popUpTo("weight_screen") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Batal", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan", color = Color.White)
            }
        }

        // 🔔 Popup konfirmasi simpan
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Berhasil") },
                text = { Text("Data berat badan berhasil disimpan!") },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                containerColor = Color.White,
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
