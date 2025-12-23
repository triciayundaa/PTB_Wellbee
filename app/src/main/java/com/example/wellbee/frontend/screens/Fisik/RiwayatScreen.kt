package com.example.wellbee.frontend.screens.Fisik

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.*
import com.example.wellbee.data.viewmodel.FisikViewModel
import com.example.wellbee.data.viewmodel.FisikViewModelFactory
import com.example.wellbee.frontend.components.showTimePicker
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
private fun rsInputColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = Color(0xFF0E4DA4),
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor = Color(0xFF0E4DA4),
    unfocusedLabelColor = Color.Gray,
    cursorColor = Color(0xFF0E4DA4)
)

@Composable
private fun rsReadOnlyColors() = OutlinedTextFieldDefaults.colors(
    disabledTextColor = Color.Black,
    disabledBorderColor = Color.LightGray,
    disabledLabelColor = Color.DarkGray,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black
)

@Composable
fun RiwayatScreen(
    navController: NavController,
    viewModel: FisikViewModel = viewModel(
        factory = FisikViewModelFactory(LocalContext.current)
    )
) {
    var selectedTab by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
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
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        when (selectedTab) {
            "Sport" -> SportRiwayatList(viewModel)
            "Sleep" -> SleepRiwayatList(viewModel)
            "Berat Badan" -> BeratBadanRiwayatList(viewModel)
            else -> PilihKategoriScreen()
        }
    }
}

@Composable
fun PilihKategoriScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Pilih kategori untuk melihat riwayat", color = Color.Gray)
    }
}

@Composable
fun SportRiwayatList(viewModel: FisikViewModel) {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()

    val vmData by viewModel.sportList.collectAsState()
    var localList by remember { mutableStateOf<List<SportHistory>>(emptyList()) }

    LaunchedEffect(vmData) {
        if (localList.isEmpty() && vmData.isNotEmpty()) {
            localList = vmData
        } else if (vmData.size != localList.size) {
            localList = vmData
        }
    }

    var editingItem by remember { mutableStateOf<SportHistory?>(null) }

    if (localList.isEmpty() && vmData.isEmpty()) {
        Text("Belum ada data olahraga.", color = Color.Gray)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(localList) { item ->
            val bitmap = rsDecodeBase64(item.foto)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(), contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    Text("Tanggal: ${rsFormatTanggalDisplay(item.tanggal)}", color = Color.Gray)
                    Text(item.jenisOlahraga, fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                    Text("${item.durasiMenit} menit", color = Color(0xFF00B894))
                    Text("${item.kaloriTerbakar} kcal", color = Color(0xFFD63031))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { editingItem = item }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00B894)) }
                        IconButton(onClick = {
                            scope.launch {
                                val resultDel = repo.deleteSport(item.id)
                                if (resultDel.isSuccess) {
                                    localList = localList.filter { it.id != item.id }
                                    viewModel.loadSportData()
                                }
                            }
                        }) { Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFD63031)) }
                    }
                }
            }
        }
    }

    if (editingItem != null) {
        RsEditSportDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                localList = localList.map { if (it.id == updatedItem.id) updatedItem else it }
                editingItem = null
                viewModel.loadSportData()
            }
        )
    }
}

@Composable
private fun RsEditSportDialog(
    item: SportHistory,
    onDismiss: () -> Unit,
    onSave: (SportHistory) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var jenis by remember { mutableStateOf(item.jenisOlahraga) }
    var durasi by remember { mutableStateOf(item.durasiMenit.toString()) }
    var kalori by remember { mutableStateOf(item.kaloriTerbakar.toString()) }
    val USER_WEIGHT_KG = 60.0

    LaunchedEffect(jenis, durasi) {
        val m = durasi.toIntOrNull() ?: 0
        kalori = if (m > 0) rsHitungKalori(jenis, m, USER_WEIGHT_KG).toInt().toString() else "0"
    }

    val FOTO_BITMAP = rsDecodeBase64(item.foto)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text("Edit Data Olahraga", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4)) },
        text = {
            Column {
                if (FOTO_BITMAP != null) {
                    Image(
                        bitmap = FOTO_BITMAP.asImageBitmap(), contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)).padding(bottom = 12.dp)
                    )
                }
                OutlinedTextField(
                    value = jenis, onValueChange = { jenis = it }, label = { Text("Jenis Olahraga") },
                    modifier = Modifier.fillMaxWidth(), colors = rsInputColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = durasi, onValueChange = { durasi = it.filter { c -> c.isDigit() } },
                    label = { Text("Durasi (menit)") }, modifier = Modifier.fillMaxWidth(), colors = rsInputColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = kalori, onValueChange = {}, readOnly = true, enabled = false,
                    label = { Text("Kalori Terbakar") }, modifier = Modifier.fillMaxWidth(), colors = rsReadOnlyColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val repo = FisikRepository(context)
                    val req = SportRequest(jenis, durasi.toInt(), kalori.toInt(), item.foto, item.tanggal ?: "")
                    scope.launch {
                        val result = repo.updateSport(item.id, req)
                        if (result.isSuccess) {
                            onSave(item.copy(jenisOlahraga = jenis, durasiMenit = durasi.toInt(), kaloriTerbakar = kalori.toInt()))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) { Text("Simpan", color = Color.White) }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))) { Text("Batal", color = Color.White) }
        }
    )
}

@Composable
fun SleepRiwayatList(viewModel: FisikViewModel) {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()
    val vmData by viewModel.sleepList.collectAsState()
    var localList by remember { mutableStateOf<List<SleepData>>(emptyList()) }

    LaunchedEffect(vmData) {
        if (localList.isEmpty() && vmData.isNotEmpty()) {
            localList = vmData
        } else if (vmData.size != localList.size) {
            localList = vmData
        }
    }

    var editingItem by remember { mutableStateOf<SleepData?>(null) }

    if (localList.isEmpty() && vmData.isEmpty()) { Text("Belum ada data tidur.", color = Color.Gray) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(localList) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tanggal: ${rsFormatTanggalDisplay(item.tanggal)}", color = Color.Gray)
                    Text("Jam Tidur: ${item.jamTidur}", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                    Text("Jam Bangun: ${item.jamBangun}", color = Color(0xFF00B894))
                    Text("Durasi: ${item.durasiTidur} jam")
                    Text("Kualitas: ${item.kualitasTidur}/5")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { editingItem = item }) { Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF00B894)) }
                        IconButton(onClick = {
                            scope.launch {
                                val res = repo.deleteSleep(item.id)
                                if (res.isSuccess) {
                                    localList = localList.filter { it.id != item.id }
                                    viewModel.loadSleepData()
                                }
                            }
                        }) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD63031)) }
                    }
                }
            }
        }
    }
    if (editingItem != null) {
        RsEditSleepDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                localList = localList.map { if (it.id == updatedItem.id) updatedItem else it }
                editingItem = null
                viewModel.loadSleepData()
            }
        )
    }
}

@Composable
private fun RsEditSleepDialog(
    item: SleepData,
    onDismiss: () -> Unit,
    onSave: (SleepData) -> Unit
) {
    val context = LocalContext.current
    val activity = context.rsGetActivity()
    val scope = rememberCoroutineScope()

    var jamTidur by remember { mutableStateOf(item.jamTidur) }
    var jamBangun by remember { mutableStateOf(item.jamBangun) }
    var durasiTidur by remember { mutableStateOf(item.durasiTidur.toString()) }
    var kualitas by remember { mutableStateOf(item.kualitasTidur) }

    var showPickerTidur by remember { mutableStateOf(false) }
    var showPickerBangun by remember { mutableStateOf(false) }

    fun updateDurasi() {
        durasiTidur = rsHitungDurasiTidur(jamTidur, jamBangun)
    }

    if (showPickerTidur && activity != null) {
        showTimePicker(activity) { selected ->
            jamTidur = selected; updateDurasi(); showPickerTidur = false
        }
    }
    if (showPickerBangun && activity != null) {
        showTimePicker(activity) { selected ->
            jamBangun = selected; updateDurasi(); showPickerBangun = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Edit Data Tidur", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4)) },
        text = {
            Column {
                OutlinedTextField(
                    value = jamTidur, onValueChange = {}, label = { Text("Jam Tidur") },
                    readOnly = true, enabled = false,
                    modifier = Modifier.fillMaxWidth().clickable { showPickerTidur = true }, colors = rsReadOnlyColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = jamBangun, onValueChange = {}, label = { Text("Jam Bangun") },
                    readOnly = true, enabled = false,
                    modifier = Modifier.fillMaxWidth().clickable { showPickerBangun = true }, colors = rsReadOnlyColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = durasiTidur, onValueChange = { }, readOnly = true, enabled = false,
                    label = { Text("Durasi (jam)") }, modifier = Modifier.fillMaxWidth(), colors = rsReadOnlyColors()
                )
                Spacer(Modifier.height(12.dp))
                RsSleepQualitySelector(selectedQuality = kualitas, onQualitySelected = { kualitas = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val repo = FisikRepository(context)
                    val fixedDurasi = durasiTidur.replace(",", ".").toDoubleOrNull() ?: 0.0

                    val tanggalBersih = cleanDateForServer(item.tanggal)
                    val req = SleepRequest(jamTidur, jamBangun, fixedDurasi, kualitas, tanggalBersih)

                    scope.launch {
                        val result = repo.updateSleep(item.id, req)
                        if (result.isSuccess) {
                            onSave(item.copy(jamTidur = jamTidur, jamBangun = jamBangun, durasiTidur = fixedDurasi, kualitasTidur = kualitas))
                        } else {
                            Toast.makeText(context, "Gagal: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) { Text("Simpan", color = Color.White) }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))) { Text("Batal", color = Color.White) }
        }
    )
}

@Composable
private fun RsSleepQualitySelector(selectedQuality: Int, onQualitySelected: (Int) -> Unit) {
    val emojis = listOf("😴", "😕", "😐", "🙂", "😄")
    val labels = listOf("Sangat Buruk", "Buruk", "Cukup", "Baik", "Sangat Baik")
    Column {
        Text("Kualitas Tidur", fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            emojis.forEachIndexed { index, emoji ->
                val score = index + 1
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).clickable { onQualitySelected(score) }
                        .background(
                            if (selectedQuality == score) Color(0xFFE0E9FF) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ).padding(8.dp)
                ) { Text(text = emoji, fontSize = 28.sp) }
            }
        }
        if (selectedQuality > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(labels[selectedQuality - 1], color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp)
        }
    }
}

@Composable
fun BeratBadanRiwayatList(viewModel: FisikViewModel) {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()
    val vmData by viewModel.weightList.collectAsState()
    var localList by remember { mutableStateOf<List<WeightData>>(emptyList()) }

    LaunchedEffect(vmData) {
        if (localList.isEmpty() && vmData.isNotEmpty()) {
            localList = vmData
        } else if (vmData.size != localList.size) {
            localList = vmData
        }
    }

    var editingItem by remember { mutableStateOf<WeightData?>(null) }

    if (localList.isEmpty() && vmData.isEmpty()) { Text("Belum ada data berat badan.", color = Color.Gray) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(localList) { item ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tanggal: ${rsFormatTanggalDisplay(item.tanggal)}", color = Color.Gray)
                    Text("Berat: ${item.beratBadan} kg", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                    Text("Tinggi: ${item.tinggiBadan} cm")
                    Text("BMI: ${String.format("%.1f", item.bmi)}")
                    Text("Kategori: ${item.kategori}", color = when (item.kategori) {
                        "Kurus" -> Color(0xFF3498DB)
                        "Normal" -> Color(0xFF00B894)
                        "Kelebihan Berat" -> Color(0xFFF1C40F)
                        else -> Color(0xFFD63031)
                    })
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { editingItem = item }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00B894)) }
                        IconButton(onClick = {
                            scope.launch {
                                val res = repo.deleteWeight(item.id)
                                if (res.isSuccess) {
                                    localList = localList.filter { it.id != item.id }
                                    viewModel.loadWeightData()
                                }
                            }
                        }) { Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD63031)) }
                    }
                }
            }
        }
    }
    if (editingItem != null) {
        RsEditWeightDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                localList = localList.map { if (it.id == updatedItem.id) updatedItem else it }
                editingItem = null
                viewModel.loadWeightData()
            }
        )
    }
}

@Composable
private fun RsEditWeightDialog(
    item: WeightData,
    onDismiss: () -> Unit,
    onSave: (WeightData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FisikRepository(context) }

    var berat by remember { mutableStateOf(item.beratBadan.toString()) }
    var tinggi by remember { mutableStateOf(item.tinggiBadan.toString()) }

    val beratVal = berat.replace(",", ".").toDoubleOrNull()
    val tinggiVal = tinggi.replace(",", ".").toDoubleOrNull()

    val bmi = remember(beratVal, tinggiVal) {
        if (beratVal != null && tinggiVal != null && tinggiVal > 0) {
            beratVal / ((tinggiVal / 100) * (tinggiVal / 100))
        } else 0.0
    }

    val kategori = when {
        bmi == 0.0 -> ""
        bmi < 18.5 -> "Kurus"
        bmi < 25 -> "Normal"
        bmi < 30 -> "Kelebihan Berat"
        else -> "Obesitas"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = { Text("Edit Berat Badan", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4)) },
        text = {
            Column {
                OutlinedTextField(
                    value = berat,
                    onValueChange = { berat = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Berat Badan (kg)") }, modifier = Modifier.fillMaxWidth(), colors = rsInputColors()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = tinggi,
                    onValueChange = { tinggi = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
                    label = { Text("Tinggi Badan (cm)") }, modifier = Modifier.fillMaxWidth(), colors = rsInputColors()
                )
                Spacer(Modifier.height(8.dp))
                Text("BMI: ${String.format("%.1f", bmi)}", color = Color.Black)
                Text("Kategori: $kategori", color = Color.Black)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (beratVal != null && tinggiVal != null && bmi > 0) {
                        val tanggalBersih = cleanDateForServer(item.tanggal)
                        val req = WeightRequest(beratVal, tinggiVal, bmi, kategori, tanggalBersih)

                        scope.launch {
                            val result = repo.updateWeight(item.id, req)
                            if (result.isSuccess) {
                                onSave(item.copy(beratBadan = beratVal, tinggiBadan = tinggiVal, bmi = bmi, kategori = kategori))
                            } else {
                                Toast.makeText(context, "Gagal simpan: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) { Text("Simpan", color = Color.White) }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))) { Text("Batal", color = Color.White) }
        }
    )
}

private fun rsFormatTanggalDisplay(input: String?): String {
    if (input.isNullOrEmpty()) return "-"
    val isoFormats = listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd")
    for (pattern in isoFormats) {
        try {
            val parser = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
            parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = parser.parse(input)
            val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
            return formatter.format(date!!)
        } catch (e: Exception) {}
    }
    return input
}

private fun rsDecodeBase64(base64: String?): Bitmap? {
    if (base64.isNullOrEmpty()) return null
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) { null }
}

private fun rsHitungDurasiTidur(jamTidur: String, jamBangun: String): String {
    if (jamTidur.isEmpty() || jamBangun.isEmpty()) return ""
    return try {
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val t1 = format.parse(jamTidur)
        val t2 = format.parse(jamBangun)
        if (t1 != null && t2 != null) {
            var diff = t2.time - t1.time
            if (diff < 0) diff += 24 * 60 * 60 * 1000
            val hours = diff / (1000 * 60 * 60).toDouble()
            String.format(java.util.Locale.US, "%.1f", hours)
        } else ""
    } catch (e: Exception) { "" }
}

private fun rsHitungKalori(jenis: String, durasiMenit: Int, beratKg: Double): Double {
    val met = when (jenis.trim().lowercase()) {
        "lari", "jogging" -> 8.3
        "jalan", "jalan cepat" -> 3.8
        "bersepeda", "sepeda" -> 7.5
        else -> 5.0
    }
    return met * 3.5 * beratKg / 200.0 * durasiMenit
}

private fun Context.rsGetActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.rsGetActivity()
    else -> null
}

private fun cleanDateForServer(rawDate: String?): String {
    if (rawDate.isNullOrEmpty()) return ""
    return if (rawDate.length >= 10) rawDate.substring(0, 10) else rawDate
}