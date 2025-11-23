package com.example.wellbee.frontend.screens.Fisik

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.frontend.components.showTimePicker
import com.example.wellbee.frontend.components.showDatePicker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SleepScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context.getActivity()

    var tanggal by remember { mutableStateOf("") }
    var jamTidur by remember { mutableStateOf("") }
    var jamBangun by remember { mutableStateOf("") }
    var durasiTidur by remember { mutableStateOf("") }
    var kualitasTidur by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
        // Judul
        Text(
            text = "Catat Kualitas Tidur Kamu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📅 Tanggal
        DateTimeField(
            label = "Tanggal",
            value = tanggal,
            onClick = {
                activity?.let {
//                    showDatePicker(it) { selectedDate ->
//                        tanggal = selectedDate
//                    }
                }
            }
        )

        // 🕒 Jam Tidur
        DateTimeField(
            label = "Jam Tidur",
            value = jamTidur,
            onClick = {
                activity?.let {
                    showTimePicker(it) { selected ->
                        jamTidur = selected
                        durasiTidur = hitungDurasiTidur(jamTidur, jamBangun)
                    }
                }
            }
        )

        // ⏰ Jam Bangun
        DateTimeField(
            label = "Jam Bangun",
            value = jamBangun,
            onClick = {
                activity?.let {
                    showTimePicker(it) { selected ->
                        jamBangun = selected
                        durasiTidur = hitungDurasiTidur(jamTidur, jamBangun)
                    }
                }
            }
        )

        // 😴 Durasi Tidur (otomatis)
        OutlinedTextField(
            value = durasiTidur,
            onValueChange = {},
            label = { Text("Durasi Tidur (jam)") },
            placeholder = { Text("Akan muncul otomatis") },
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🌙 Kualitas Tidur → tampil hanya kalau tanggal sudah dipilih
        if (tanggal.isNotEmpty()) {
            Text(
                text = "Kualitas Tidur",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val emojis = listOf("😴", "😕", "😐", "🙂", "😄")
                emojis.forEachIndexed { index, emoji ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { kualitasTidur = index + 1 }
                            .background(
                                if (kualitasTidur == index + 1) Color(0xFFE0E9FF)
                                else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (kualitasTidur > 0) {
                val labels = listOf("Sangat Buruk", "Buruk", "Cukup", "Baik", "Sangat Baik")
                Text(
                    text = labels[kualitasTidur - 1],
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Tombol Batal & Simpan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    tanggal = ""
                    jamTidur = ""
                    jamBangun = ""
                    durasiTidur = ""
                    kualitasTidur = 0
                    navController.navigate("dashboard") {
                        popUpTo("sport_screen") { inclusive = true }
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

        // Pop-up Berhasil Simpan
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Berhasil") },
                text = { Text("Data tidur berhasil disimpan!") },
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

/**
 * Komponen reusable untuk field tanggal/jam
 */
@Composable
fun DateTimeField(label: String, value: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text("Klik untuk memilih") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )
    }
}

/**
 * Hitung durasi tidur otomatis
 */
fun hitungDurasiTidur(jamTidur: String, jamBangun: String): String {
    if (jamTidur.isEmpty() || jamBangun.isEmpty()) return ""
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    val waktuTidur = format.parse(jamTidur)
    val waktuBangun = format.parse(jamBangun)
    var selisih = (waktuBangun.time - waktuTidur.time).toDouble()
    if (selisih < 0) selisih += 24 * 60 * 60 * 1000
    val jam = selisih / (1000 * 60 * 60)
    return String.format("%.1f", jam)
}

/**
 * Helper: ambil Activity dari Context
 */
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
