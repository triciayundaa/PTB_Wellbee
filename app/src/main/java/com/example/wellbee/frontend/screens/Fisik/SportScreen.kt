package com.example.wellbee.frontend.screens.Fisik

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.frontend.components.DateField
import com.example.wellbee.frontend.components.showDatePicker
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun SportScreen(navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // INPUT STATE
    var jenisOlahraga by remember { mutableStateOf("") }
    var durasi by remember { mutableStateOf("") }
    var kalori by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    // FOTO STATE
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBase64 by remember { mutableStateOf<String?>(null) }

    //Tanggal
    var tanggal by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // Constants
    val USER_WEIGHT_KG = 60.0
    LaunchedEffect(showDatePickerDialog) {
        if (showDatePickerDialog) {
            showDatePicker(context) { selectedDate ->
                tanggal = selectedDate
                showDatePickerDialog = false
            }
        }
    }



    // KAMERA LAUNCHER
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val uri = saveImageToGallery(context, bitmap)
            imageUri = uri
            fotoBase64 = bitmapToBase64(bitmap) // WAJIB UNTUK BACKEND
        }
    }

    // AUTO HITUNG KALORI
    LaunchedEffect(jenisOlahraga, durasi) {
        val menit = durasi.toIntOrNull() ?: 0
        kalori = if (menit > 0) {
            "${hitungKaloriTerbakar(jenisOlahraga, menit, USER_WEIGHT_KG).roundToInt()} kcal"
        } else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Tambahkan Data Olahraga",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TANGGAL
        DateField(
            label = "Tanggal",
            value = tanggal,
            onClick = { showDatePickerDialog = true }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Input Jenis Olahraga
        OutlinedTextField(
            value = jenisOlahraga,
            onValueChange = { jenisOlahraga = it },
            label = { Text("Jenis Olahraga") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF0E4DA4),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF0E4DA4),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input Durasi
        OutlinedTextField(
            value = durasi,
            onValueChange = { durasi = it.filter { c -> c.isDigit() } },
            label = { Text("Durasi (menit)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF0E4DA4),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF0E4DA4),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input Kalori (Read Only)
        OutlinedTextField(
            value = kalori,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Kalori Terbakar") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.DarkGray,
                disabledBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ======= TOMBOL AMBIL FOTO =======
        Button(
            onClick = { cameraLauncher.launch() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Ambil Foto", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PREVIEW FOTO
        imageUri?.let { uri ->
            Text("Foto aktivitas olahraga:", fontWeight = FontWeight.Bold)

            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Preview Foto Olahraga",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =============== BUTTON SAVE / CANCEL =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Batal
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Batal", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    if (jenisOlahraga.isEmpty() || durasi.isEmpty()) {
                        Toast.makeText(context, "Isi semua data!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    val request = SportRequest(
                        jenisOlahraga = jenisOlahraga,
                        durasiMenit = durasi.toInt(),
                        kaloriTerbakar = kalori.replace(" kcal", "").toIntOrNull() ?: 0,
                        foto = fotoBase64,
                        tanggal = tanggal
                    )

                    scope.launch {
                        try {
                            val api = RetrofitClient.getInstance(context)
                            val response = api.catatOlahraga(request)

                            isLoading = false

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                // Reset Form
                                jenisOlahraga = ""
                                durasi = ""
                                kalori = ""
                                tanggal = ""
                                imageUri = null
                                fotoBase64 = null
                                // Opsional: Kembali ke halaman sebelumnya
                                // navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isLoading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else
                    Text("Simpan", color = Color.White)
            }
        }
    }
}

/* =======================================================
       FUNGSI TAMBAHAN (HELPER)
======================================================= */

fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri {
    val filename = "sport_photo_${System.currentTimeMillis()}.jpg"
    // Menyimpan ke internal storage aplikasi agar tidak perlu permission WRITE_EXTERNAL_STORAGE
    val file = File(context.getExternalFilesDir(null), filename)

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }

    // Pastikan FileProvider sudah diatur di AndroidManifest.xml
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val output = ByteArrayOutputStream()
    // Kompresi kualitas 100 (bisa diturunkan jika string terlalu panjang)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
    val bytes = output.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun hitungKaloriTerbakar(jenis: String, durasiMenit: Int, beratKg: Double): Double {
    val met = when (jenis.trim().lowercase()) {
        "lari", "jogging" -> 8.3
        "jalan", "jalan cepat" -> 3.8
        "bersepeda", "sepeda" -> 7.5
        "renang" -> 6.0
        "yoga" -> 3.0
        "skipping", "lompat tali" -> 12.3
        "basket" -> 6.5
        "futsal", "sepak bola" -> 7.0
        "badminton", "bulu tangkis" -> 5.5
        else -> 5.0
    }
    // Rumus METs: Kalori = MET * 3.5 * BB(kg) / 200 * durasi(menit)
    return met * 3.5 * beratKg / 200.0 * durasiMenit
}