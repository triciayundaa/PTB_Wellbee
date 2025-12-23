package com.example.wellbee.frontend.screens.Fisik

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wellbee.data.viewmodel.FisikViewModel
import com.example.wellbee.data.viewmodel.FisikViewModelFactory
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.frontend.components.DateField
import com.example.wellbee.frontend.components.showDatePicker
import com.example.wellbee.frontend.components.showTimePicker
import java.text.SimpleDateFormat
import java.util.*


private object SleepColors {
    val Primary = Color(0xFF0E4DA4)
    val Secondary = Color(0xFF74B9FF)
    val Background = Color(0xFFF7F9FB)
    val Surface = Color.White
    val SelectedItem = Color(0xFFE0E9FF)
    val TextPrimary = Color(0xFF0E4DA4)
}

@Composable
fun SleepScreen(
    navController: NavHostController,

    viewModel: FisikViewModel = viewModel(
        factory = FisikViewModelFactory(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val activity = context.getActivity()
    val scrollState = rememberScrollState()

    val isLoading by viewModel.isLoading.collectAsState()

    var tanggal by remember { mutableStateOf("") }
    var jamTidur by remember { mutableStateOf("") }
    var jamBangun by remember { mutableStateOf("") }
    var durasiTidur by remember { mutableStateOf("") }
    var kualitasTidur by remember { mutableStateOf(0) }

    var showPickerTidur by remember { mutableStateOf(false) }
    var showPickerBangun by remember { mutableStateOf(false) }

    var showDatePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDatePickerDialog) {
        if (showDatePickerDialog) {
            showDatePicker(context) { selectedDate ->
                tanggal = selectedDate
                showDatePickerDialog = false
            }
        }
    }

    fun updateDurasi() {
        durasiTidur = hitungDurasiTidur(jamTidur, jamBangun)
    }

    if (showPickerTidur && activity != null) {
        showTimePicker(activity) { selected ->
            jamTidur = selected
            updateDurasi()
            showPickerTidur = false
        }
    }

    if (showPickerBangun && activity != null) {
        showTimePicker(activity) { selected ->
            jamBangun = selected
            updateDurasi()
            showPickerBangun = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleepColors.Background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Text(
            text = "Catat Kualitas Tidur Kamu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SleepColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        DateField(
            label = "Tanggal",
            value = tanggal,
            onClick = { showDatePickerDialog = true }
        )

        DateTimeField(
            label = "Jam Tidur",
            value = jamTidur,
            onClick = { showPickerTidur = true }
        )

        DateTimeField(
            label = "Jam Bangun",
            value = jamBangun,
            onClick = { showPickerBangun = true }
        )

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
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (jamTidur.isNotEmpty() && jamBangun.isNotEmpty()) {
            SleepQualitySelector(
                selectedQuality = kualitasTidur,
                onQualitySelected = { kualitasTidur = it }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (isLoading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SleepColors.Primary)
            }
        } else {
            SleepActionButtons(
                onCancel = {
                    navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                },
                onSave = {
                    if (tanggal.isEmpty()) {
                        Toast.makeText(context, "Tanggal wajib diisi", Toast.LENGTH_SHORT).show()
                        return@SleepActionButtons
                    }
                    if (jamTidur.isEmpty() || jamBangun.isEmpty() || durasiTidur.isEmpty()) {
                        Toast.makeText(context, "Lengkapi data tidur", Toast.LENGTH_SHORT).show()
                        return@SleepActionButtons
                    }

                    val req = SleepRequest(
                        jamTidur = jamTidur,
                        jamBangun = jamBangun,
                        durasiTidur = durasiTidur.replace(",", ".").toDouble(),
                        kualitasTidur = kualitasTidur,
                        tanggal = tanggal
                    )

                    viewModel.catatTidur(
                        req = req,
                        onSuccess = {
                            Toast.makeText(context, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            tanggal = ""
                            jamTidur = ""
                            jamBangun = ""
                            durasiTidur = ""
                            kualitasTidur = 0
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun SleepQualitySelector(selectedQuality: Int, onQualitySelected: (Int) -> Unit) {
    val emojis = listOf("😴", "😕", "😐", "🙂", "😄")
    val labels = listOf("Sangat Buruk", "Buruk", "Cukup", "Baik", "Sangat Baik")

    Column {
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
            emojis.forEachIndexed { index, emoji ->
                val score = index + 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onQualitySelected(score) }
                        .background(
                            color = if (selectedQuality == score) SleepColors.SelectedItem else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(text = emoji, fontSize = 28.sp)
                }
            }
        }

        if (selectedQuality > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = labels[selectedQuality - 1],
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SleepActionButtons(onCancel: () -> Unit, onSave: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = SleepColors.Secondary),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Batal", color = Color.White)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = SleepColors.Primary),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Simpan", color = Color.White)
        }
    }
}

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
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledLabelColor = Color.Gray,
                disabledBorderColor = Color.Gray
            )
        )
    }
}

fun hitungDurasiTidur(jamTidur: String, jamBangun: String): String {
    if (jamTidur.isEmpty() || jamBangun.isEmpty()) return ""
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val t1 = format.parse(jamTidur)
        val t2 = format.parse(jamBangun)

        if (t1 != null && t2 != null) {
            var diff = t2.time - t1.time
            if (diff < 0) diff += 24 * 60 * 60 * 1000
            val hours = diff / (1000 * 60 * 60).toDouble()
            String.format(Locale.US, "%.1f", hours)
        } else ""
    } catch (e: Exception) {
        ""
    }
}

fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}