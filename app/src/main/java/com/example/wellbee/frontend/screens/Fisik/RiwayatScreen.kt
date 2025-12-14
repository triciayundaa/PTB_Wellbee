package com.example.wellbee.frontend.screens.Fisik

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.frontend.components.showTimePicker
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.wellbee.data.model.WeightData
import com.example.wellbee.data.model.WeightRequest

@Composable
fun RiwayatScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf<String?>(null) }

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

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Date", color = Color.White)
            }
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
            "Sport" -> SportRiwayatList()
            "Sleep" -> SleepRiwayatList()
            "Berat Badan" -> BeratBadanRiwayatList()
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

/* ============================================================
   SPORT RIWAYAT (DENGAN FOTO)
   ============================================================ */

@Composable
fun SportRiwayatList() {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }

    var data by remember { mutableStateOf<List<SportHistory>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var editingItem by remember { mutableStateOf<SportHistory?>(null) }

    val prefs = context.getSharedPreferences("wellbee_prefs", Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

    LaunchedEffect(Unit) {
        val result = repo.getSportHistory(userId)
        if (result.isSuccess) data = result.getOrNull()!!
        loading = false
    }

    if (loading) {
        Text("Loading...")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(data) { item ->

            val bitmap = decodeBase64(item.foto)

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text("Tanggal: ${formatTanggalDisplay(item.tanggal)}", color = Color.Gray)
                    Text(item.jenisOlahraga, fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                    Text("${item.durasiMenit} menit", color = Color(0xFF00B894))
                    Text("${item.kaloriTerbakar} kcal", color = Color(0xFFD63031))   // <-- TAMBAHKAN INI

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { editingItem = item }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00B894))
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val result = repo.deleteSport(item.id)
                                if (result.isSuccess) data = data.filter { it.id != item.id }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFD63031))
                        }
                    }
                }
            }
        }
    }


    if (editingItem != null) {
        EditSportDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                data = data.map { if (it.id == updated.id) updated else it }
                editingItem = null
            }
        )
    }
}

fun formatTanggalDisplay(input: String?): String {
    if (input.isNullOrEmpty()) return "-"

    // Buat parser ISO 8601 (dari Retrofit)
    val isoFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )

    for (pattern in isoFormats) {
        try {
            val parser = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault())
            parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = parser.parse(input)

            val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
            return formatter.format(date!!)
        } catch (e: Exception) {
        }
    }

    return input // fallback
}

/* ============================================================
   POPUP EDIT SPORT (DENGAN FOTO)
   ============================================================ */

@Composable
fun EditSportDialog(
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

    // AUTO UPDATE KALORI
    LaunchedEffect(jenis, durasi) {
        val m = durasi.toIntOrNull() ?: 0
        kalori = if (m > 0) {
            hitungKaloriTerbakarRiwayat(jenis, m, USER_WEIGHT_KG).toInt().toString()
        } else "0"
    }

    val FOTO_BITMAP = decodeBase64(item.foto)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text("Edit Data Olahraga", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0E4DA4))
        },
        text = {
            Column {

                if (FOTO_BITMAP != null) {
                    Image(
                        bitmap = FOTO_BITMAP.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 12.dp)
                    )
                }

                OutlinedTextField(
                    value = jenis,
                    onValueChange = { jenis = it },
                    label = { Text("Jenis Olahraga") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durasi,
                    onValueChange = { durasi = it.filter { c -> c.isDigit() } },
                    label = { Text("Durasi (menit)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kalori,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Kalori Terbakar") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val repo = FisikRepository(context)
                    val req = SportRequest(
                        jenisOlahraga = jenis,
                        durasiMenit = durasi.toInt(),
                        kaloriTerbakar = kalori.toInt(),
                        foto = item.foto,
                        tanggal = item.tanggal ?: ""
                    )

                    scope.launch {
                        val result = repo.updateSport(item.id, req)
                        if (result.isSuccess) {
                            onSave(
                                item.copy(
                                    jenisOlahraga = jenis,
                                    durasiMenit = durasi.toInt(),
                                    kaloriTerbakar = kalori.toInt()
                                )
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) {
                Text("Simpan", color = Color.White)
            }
        },

        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))
            ) {
                Text("Batal", color = Color.White)
            }
        }
    )
}

/* ============================================================
   DECODE BASE64 FOTO
   ============================================================ */

fun decodeBase64(base64: String?): Bitmap? {
    if (base64.isNullOrEmpty()) return null
    return try {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        null
    }
}

/* ============================================================
   SLEEP DAN BERAT BADAN (TIDAK DIUBAH)
   ============================================================ */

@Composable
fun SleepRiwayatList() {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }

    var data by remember { mutableStateOf<List<SleepData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    var editingItem by remember { mutableStateOf<SleepData?>(null) }

    LaunchedEffect(Unit) {
        val result = repo.getSleepHistory()
        if (result.isSuccess) {
            data = result.getOrNull()!!
        }
        loading = false
    }

    if (loading) {
        Text("Loading...")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(data) { item ->

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(Modifier.padding(16.dp)) {
                    Text("Tanggal: ${formatTanggalDisplay(item.tanggal)}", color = Color.Gray)
                    Text("Jam Tidur: ${item.jamTidur}", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                    Text("Jam Bangun: ${item.jamBangun}", color = Color(0xFF00B894))
                    Text("Durasi: ${item.durasiTidur} jam")
                    Text("Kualitas: ${item.kualitasTidur}/5")

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { editingItem = item }) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF00B894))
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val res = repo.deleteSleep(item.id)
                                if (res.isSuccess) {
                                    data = data.filter { it.id != item.id }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD63031))
                        }
                    }
                }
            }
        }
    }

    if (editingItem != null) {
        EditSleepDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                data = data.map { if (it.id == updated.id) updated else it }
                editingItem = null
            }
        )
    }
}

@Composable
fun EditSleepDialog(
    item: SleepData,
    onDismiss: () -> Unit,
    onSave: (SleepData) -> Unit
) {
    val context = LocalContext.current
    val activity = context.getActivity()
    val scope = rememberCoroutineScope()

    var jamTidur by remember { mutableStateOf(item.jamTidur) }
    var jamBangun by remember { mutableStateOf(item.jamBangun) }
    var durasiTidur by remember { mutableStateOf(item.durasiTidur.toString()) }
    var kualitas by remember { mutableStateOf(item.kualitasTidur) }

    var showPickerTidur by remember { mutableStateOf(false) }
    var showPickerBangun by remember { mutableStateOf(false) }

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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,         // <-- INI WAJIB
        shape = RoundedCornerShape(20.dp),    // <-- AGAR MELENGKUNG CANTIK
        title = {
            Text(
                "Edit Data Tidur",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0E4DA4)
            )
        },
        text = {
            Column {

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
                    onValueChange = { },
                    readOnly = true,
                    enabled = false,
                    label = { Text("Durasi (jam)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                SleepQualitySelector(
                    selectedQuality = kualitas,
                    onQualitySelected = { kualitas = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = item.copy(
                        jamTidur = jamTidur,
                        jamBangun = jamBangun,
                        durasiTidur = durasiTidur.toDouble(),
                        kualitasTidur = kualitas
                    )

                    val repo = FisikRepository(context)
                    val req = SleepRequest(
                        jamTidur = jamTidur,
                        jamBangun = jamBangun,
                        durasiTidur = durasiTidur.toDouble(),
                        kualitasTidur = kualitas,
                        tanggal = item.tanggal ?: ""
                    )

                    scope.launch {
                        val result = repo.updateSleep(item.id, req)
                        if (result.isSuccess) onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4))
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

fun hitungKaloriTerbakarRiwayat(jenis: String, durasiMenit: Int, beratKg: Double): Double {
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
    return met * 3.5 * beratKg / 200.0 * durasiMenit
}

@Composable
fun BeratBadanRiwayatList() {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<List<WeightData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var editingItem by remember { mutableStateOf<WeightData?>(null) }

    LaunchedEffect(Unit) {
        val result = repo.getWeightHistory()
        if (result.isSuccess) {
            data = result.getOrNull()!!
        }
        loading = false
    }

    if (loading) {
        Text("Loading...")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(data) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        "Tanggal: ${formatTanggalDisplay(item.tanggal)}",
                        color = Color.Gray
                    )

                    Text(
                        "Berat: ${item.beratBadan} kg",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0E4DA4)
                    )

                    Text("Tinggi: ${item.tinggiBadan} cm")
                    Text("BMI: ${String.format("%.1f", item.bmi)}")
                    Text(
                        "Kategori: ${item.kategori}",
                        color = when (item.kategori) {
                            "Kurus" -> Color(0xFF3498DB)
                            "Normal" -> Color(0xFF00B894)
                            "Kelebihan Berat" -> Color(0xFFF1C40F)
                            else -> Color(0xFFD63031)
                        }
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { editingItem = item }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF00B894)
                            )
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val res = repo.deleteWeight(item.id)
                                if (res.isSuccess) {
                                    data = data.filter { it.id != item.id }
                                }
                            }
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFD63031)
                            )
                        }
                    }
                }
            }
        }
    }

    // POPUP EDIT
    if (editingItem != null) {
        EditWeightDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                data = data.map { if (it.id == updated.id) updated else it }
                editingItem = null
            }
        )
    }
}

@Composable
fun EditWeightDialog(
    item: WeightData,
    onDismiss: () -> Unit,
    onSave: (WeightData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { FisikRepository(context) }

    var berat by remember { mutableStateOf(item.beratBadan.toString()) }
    var tinggi by remember { mutableStateOf(item.tinggiBadan.toString()) }

    val beratVal = berat.toDoubleOrNull()
    val tinggiVal = tinggi.toDoubleOrNull()

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
        title = {
            Text(
                "Edit Berat Badan",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E4DA4)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = berat,
                    onValueChange = { berat = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Berat Badan (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tinggi,
                    onValueChange = { tinggi = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Tinggi Badan (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text("BMI: ${String.format("%.1f", bmi)}")
                Text("Kategori: $kategori")
            }
        },
        confirmButton = {
            Button(
                enabled = beratVal != null && tinggiVal != null && bmi > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                onClick = {
                    val req = WeightRequest(
                        beratBadan = beratVal!!,
                        tinggiBadan = tinggiVal!!,
                        bmi = bmi,
                        kategori = kategori,
                        tanggal = item.tanggal
                    )

                    scope.launch {
                        val result = repo.updateWeight(item.id, req)
                        if (result.isSuccess) {
                            onSave(
                                item.copy(
                                    beratBadan = req.beratBadan,
                                    tinggiBadan = req.tinggiBadan,
                                    bmi = req.bmi,
                                    kategori = req.kategori
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}



