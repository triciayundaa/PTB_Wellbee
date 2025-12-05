package com.example.wellbee.frontend.screens.Fisik

import android.Manifest
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
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
// Import Retrofit dan Model
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

@Composable
fun SportScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ===== State Input =====
    var jenisOlahraga by remember { mutableStateOf("") }
    var durasi by remember { mutableStateOf("") } // string input
    var tanggal by remember { mutableStateOf("") }
    var kalori by remember { mutableStateOf("") } // hasil auto

    // ===== State UI =====
    var isLoading by remember { mutableStateOf(false) } // Untuk loading spinner

    // User ID Hardcode sementara (Ganti nanti jika sudah ada login session)
    val currentUserId = "user_123"
    val USER_WEIGHT_KG = 60.0

    // Recalc kalori setiap jenis/durasi berubah
    LaunchedEffect(jenisOlahraga, durasi) {
        val menit = durasi.toIntOrNull() ?: 0
        kalori = if (menit > 0) {
            val kcal = hitungKaloriTerbakar(jenisOlahraga, menit, USER_WEIGHT_KG)
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

        // 🏃 Jenis Olahraga
        OutlinedTextField(
            value = jenisOlahraga,
            onValueChange = { jenisOlahraga = it },
            label = { Text("Jenis Olahraga") },
            placeholder = { Text("Misalnya: Lari, Bersepeda, Yoga...") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ⏱️ Durasi (menit)
        OutlinedTextField(
            value = durasi,
            onValueChange = { input ->
                val sanitized = input.filter { it.isDigit() }
                durasi = sanitized
            },
            label = { Text("Durasi (menit)") },
            placeholder = { Text("Masukkan durasi latihan") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 📅 Tanggal (Sementara ReadOnly)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = tanggal,
                onValueChange = {},
                label = { Text("Tanggal (Belum aktif)") },
                placeholder = { Text("Pilih tanggal latihan") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔥 Kalori Terbakar
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
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.LightGray
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
            // Tombol Batal
            Button(
                onClick = {
                    navController.navigate("dashboard") { // Atau popBackStack()
                        popUpTo("sport_screen") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading // Disable saat loading
            ) { Text("Batal", color = Color.White) }

            Spacer(modifier = Modifier.width(16.dp))

            // Tombol Simpan (Dengan Logika Backend)
            Button(
                onClick = {
                    // 1. Validasi Input
                    if (jenisOlahraga.isEmpty() || durasi.isEmpty()) {
                        Toast.makeText(context, "Mohon isi jenis & durasi!", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true

                        // 2. Siapkan Data (Bersihkan string)
                        val kaloriInt = kalori.replace(" kcal", "").trim().toIntOrNull() ?: 0
                        val durasiInt = durasi.toIntOrNull() ?: 0

                        val request = SportRequest(
                            userId = currentUserId,
                            jenisOlahraga = jenisOlahraga,
                            durasiMenit = durasiInt,
                            kaloriTerbakar = kaloriInt
                        )

                        // 3. Panggil API Backend
                        RetrofitClient.getInstance(context).catatOlahraga(request).enqueue(object : Callback<SportResponse> {
                            override fun onResponse(call: Call<SportResponse>, response: Response<SportResponse>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()

                                    // Reset Form
                                    jenisOlahraga = ""
                                    durasi = ""

                                    // Opsional: Kembali ke dashboard setelah sukses
                                    // navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<SportResponse>, t: Throwable) {
                                isLoading = false
                                Toast.makeText(context, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading // Disable tombol biar gak di-klik 2x
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Simpan", color = Color.White)
                }
            }
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
        else -> 5.0
    }
    return met * 3.5 * beratKg / 200.0 * durasiMenit
}