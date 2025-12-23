package com.example.wellbee.frontend.screens.Fisik

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellbee.data.viewmodel.FisikViewModel
import com.example.wellbee.data.viewmodel.FisikViewModelFactory
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.frontend.components.DateField
import com.example.wellbee.frontend.components.showDatePicker
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun SportScreen(
    navController: NavHostController,

    viewModel: FisikViewModel = viewModel(
        factory = FisikViewModelFactory(LocalContext.current)
    )
) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isLoading by viewModel.isLoading.collectAsState()

    var jenisOlahraga by remember { mutableStateOf("") }
    var durasi by remember { mutableStateOf("") }
    var kalori by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBase64 by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    val USER_WEIGHT_KG = 60.0

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            showDatePicker(context) {
                tanggal = it
                showDatePicker = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            imageUri = saveImageToGallery(context, it)
            fotoBase64 = bitmapToBase64(it)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(jenisOlahraga, durasi) {
        val menit = durasi.toIntOrNull() ?: 0
        kalori = if (menit > 0)
            "${hitungKaloriTerbakar(jenisOlahraga, menit, USER_WEIGHT_KG).roundToInt()} kcal"
        else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Text(
            "Tambahkan Data Olahraga",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E4DA4)
        )

        Spacer(Modifier.height(16.dp))

        DateField("Tanggal", tanggal) { showDatePicker = true }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = jenisOlahraga,
            onValueChange = { jenisOlahraga = it },
            label = { Text("Jenis Olahraga") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF0E4DA4),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF0E4DA4)
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = durasi,
            onValueChange = { durasi = it.filter(Char::isDigit) },
            label = { Text("Durasi (menit)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color(0xFF0E4DA4),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF0E4DA4),
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color(0xFF0E4DA4)
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = kalori,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("Kalori Terbakar") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
                disabledLabelColor = Color.DarkGray
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B894))
        ) {
            Text("📷 Ambil Foto", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Button(
                onClick = {
                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))
            ) {
                Text("Batal", color = Color.White)
            }

            Button(
                onClick = {
                    if (jenisOlahraga.isBlank() || durasi.isBlank()) {
                        Toast.makeText(context, "Lengkapi data!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val req = SportRequest(
                        jenisOlahraga = jenisOlahraga,
                        durasiMenit = durasi.toInt(),
                        kaloriTerbakar = kalori.replace(" kcal", "").toInt(),
                        foto = fotoBase64,
                        tanggal = tanggal
                    )

                    viewModel.catatOlahraga(
                        req = req,
                        onSuccess = {
                            Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()

                            jenisOlahraga = ""
                            durasi = ""
                            kalori = ""
                            tanggal = ""
                            imageUri = null
                            fotoBase64 = null
                        }
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) {
                if (isLoading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                else
                    Text("Simpan", color = Color.White)
            }
        }
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.getExternalFilesDir(null), "sport_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
}

fun hitungKaloriTerbakar(jenis: String, durasi: Int, berat: Double): Double {
    val met = when (jenis.lowercase()) {
        "lari", "jogging" -> 8.3
        "jalan" -> 3.8
        "bersepeda" -> 7.5
        else -> 5.0
    }
    return met * 3.5 * berat / 200 * durasi
}