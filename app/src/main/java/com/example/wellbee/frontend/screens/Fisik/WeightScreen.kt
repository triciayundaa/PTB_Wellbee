@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.wellbee.frontend.screens.Fisik

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.WeightRequest
import com.example.wellbee.frontend.components.DateField
import com.example.wellbee.frontend.components.showDatePicker
import kotlinx.coroutines.launch

@Composable
fun WeightScreen(navController: NavHostController) {

    val scrollState = rememberScrollState()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()

    // ================= STATE =================
    var berat by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    var bmi by remember { mutableStateOf(0.0) }
    var kategori by remember { mutableStateOf("") }

    // ================= PARSE INPUT =================
    val beratVal = berat.replace(",", ".").toDoubleOrNull()
    val tinggiVal = tinggi.replace(",", ".").toDoubleOrNull()

    // ================= HITUNG BMI =================
    LaunchedEffect(beratVal, tinggiVal) {
        if (beratVal != null && tinggiVal != null && tinggiVal > 0) {
            val bmiValue = beratVal / ((tinggiVal / 100) * (tinggiVal / 100))
            bmi = bmiValue
            kategori = when {
                bmiValue < 18.5 -> "Kurus"
                bmiValue < 25 -> "Normal"
                bmiValue < 30 -> "Kelebihan Berat"
                else -> "Obesitas"
            }
        } else {
            bmi = 0.0
            kategori = ""
        }
    }

    // ================= VALIDASI =================
    val isValid = beratVal != null &&
            tinggiVal != null &&
            beratVal > 0 &&
            tinggiVal > 0 &&
            tanggal.isNotBlank()

    // ================= UI =================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Text(
            text = "Catat Berat & Tinggi Badan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = berat,
            onValueChange = {
                berat = it.replace(",", ".")
                    .filter { c -> c.isDigit() || c == '.' }
            },
            label = { Text("Berat Badan (kg)") },
            placeholder = { Text("Contoh: 65.5") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = tinggi,
            onValueChange = {
                tinggi = it.replace(",", ".")
                    .filter { c -> c.isDigit() || c == '.' }
            },
            label = { Text("Tinggi Badan (cm)") },
            placeholder = { Text("Contoh: 170") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        DateField(
            label = "Tanggal Pengukuran",
            value = tanggal,
            onClick = {
                activity?.let {
                    showDatePicker(it) { tanggal = it }
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        if (bmi > 0) {
            val cardColor = when (kategori) {
                "Kurus" -> Color(0xFFD6EAF8)
                "Normal" -> Color(0xFFDFFFE3)
                "Kelebihan Berat" -> Color(0xFFFFF7D6)
                else -> Color(0xFFFFD6D6)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BMI Anda", fontWeight = FontWeight.Bold)
                    Text(String.format("%.1f", bmi), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(kategori)
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))
            ) {
                Text("Batal", color = Color.White)
            }

            Spacer(Modifier.width(16.dp))

            Button(
                // 🔥 HAPUS 'enabled = isValid' AGAR TOMBOL SELALU AKTIF (BIRU)
                onClick = {
                    // 🔥 PINDAHKAN PENGECEKAN KE SINI
                    if (isValid) {
                        val req = WeightRequest(
                            beratBadan = beratVal!!,
                            tinggiBadan = tinggiVal!!,
                            bmi = bmi,
                            kategori = kategori,
                            tanggal = tanggal
                        )

                        scope.launch {
                            val result = repo.catatWeight(req)
                            if (result.isSuccess) {
                                Toast.makeText(context, "Berhasil disimpan", Toast.LENGTH_SHORT).show()

                                // Reset Input
                                berat = ""
                                tinggi = ""
                                tanggal = ""
                            } else {
                                Toast.makeText(context, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // TAMPILKAN PESAN JIKA DATA BELUM LENGKAP
                        Toast.makeText(context, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0E4DA4),
                    contentColor = Color.White
                )
            ) {
                Text("Simpan")
            }

        }
    }
}