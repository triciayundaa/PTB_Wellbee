package com.example.wellbee.frontend.screens.Fisik

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.wellbee.frontend.components.showDatePicker
import kotlin.math.roundToInt

@Composable
fun SportScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ===== State =====
    var jenisOlahraga by remember { mutableStateOf("") }
    var durasi by remember { mutableStateOf("") } // menit (string biar gampang input)
    var tanggal by remember { mutableStateOf("") }
    var kalori by remember { mutableStateOf("") } // hasil auto
    var showDialog by remember { mutableStateOf(false) }

    // Atur berat badan default untuk estimasi (nanti bisa diambil dari DB / profil user)
    val USER_WEIGHT_KG = 60.0

    // Recalc kalori setiap jenis/durasi berubah
    LaunchedEffect(jenisOlahraga, durasi) {
        val menit = durasi.toIntOrNull() ?: 0
        kalori = if (menit > 0) {
            val kcal = hitungKaloriTerbakar(jenisOlahraga, menit, USER_WEIGHT_KG)
            // tampilkan tanpa koma panjang
            "${kcal.roundToInt()} kcal"
        } else ""
    }

    // Launcher izin kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> if (granted) openCamera(context) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
        Text(
            text = "Tambahkan Data Olahraga",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🏃 Jenis Olahraga (teks hitam)
        OutlinedTextField(
            value = jenisOlahraga,
            onValueChange = { jenisOlahraga = it },
            label = { Text("Jenis Olahraga") },
            placeholder = { Text("Misalnya: Lari, Bersepeda, Yoga...") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black), // ✅ teks input hitam
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ⏱️ Durasi (menit) (teks hitam)
        OutlinedTextField(
            value = durasi,
            onValueChange = { input ->
                // hanya angka
                val sanitized = input.filter { it.isDigit() }
                durasi = sanitized
            },
            label = { Text("Durasi (menit)") },
            placeholder = { Text("Masukkan durasi latihan") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black), // ✅ teks input hitam
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 📅 Tanggal (klik untuk pilih)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
//                    showDatePicker(context) { selectedDate ->
//                        tanggal = selectedDate
//                    }
                }
        ) {
            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                label = { Text("Tanggal") },
                placeholder = { Text("Pilih tanggal latihan") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.LightGray,
                    disabledTextColor = Color.Black,
                    disabledLabelColor = Color.Gray,
                    disabledPlaceholderColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 Kalori Terbakar (otomatis, non-editable)
        OutlinedTextField(
            value = kalori,
            onValueChange = {},
            label = { Text("Kalori Terbakar") },
            placeholder = { Text("Akan dihitung otomatis") },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledPlaceholderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📸 Tombol Kamera
        Button(
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("📷 Open Camera", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Tombol Batal & Simpan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    // reset
                    jenisOlahraga = ""
                    durasi = ""
                    tanggal = ""
                    kalori = ""
                    navController.navigate("dashboard") {
                        popUpTo("sport_screen") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Batal", color = Color.White) }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    // nanti: simpan ke DB
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Simpan", color = Color.White) }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
                    ) { Text("OK", color = Color.White) }
                },
                title = { Text("Berhasil") },
                text = { Text("Data olahraga berhasil disimpan!") },
                containerColor = Color.White,
                tonalElevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// 📸 Buka kamera
fun openCamera(context: Context) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    context.startActivity(intent)
}

/**
 * Hitung kalori terbakar berbasis MET.
 * Rumus: Kalori = MET * 3.5 * berat(kg) / 200 * durasi(menit)
 * Catatan: MET disederhanakan per jenis olahraga.
 */
fun hitungKaloriTerbakar(jenis: String, durasiMenit: Int, beratKg: Double): Double {
    val met = when (jenis.trim().lowercase()) {
        "lari", "jogging", "lari jogging" -> 8.3
        "jalan", "jalan cepat" -> 3.8
        "bersepeda", "sepeda", "cycling" -> 7.5
        "renang", "berenang" -> 6.0
        "yoga" -> 3.0
        "skipping", "lompat tali" -> 12.3
        "basket", "basketball" -> 6.5
        "futsal", "sepak bola", "sepakbola" -> 7.0
        "badminton", "bulu tangkis", "bulutangkis" -> 5.5
        else -> 5.0 // default MET moderat jika jenis tak dikenali
    }
    return met * 3.5 * beratKg / 200.0 * durasiMenit
}
