package com.example.wellbee.frontend.screens.Fisik

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wellbee.data.FisikRepository
import com.example.wellbee.data.model.SportHistory
import com.example.wellbee.data.model.SportResponse
import com.example.wellbee.data.model.SleepData
import com.example.wellbee.data.model.SleepRequest
import com.example.wellbee.data.model.SleepResponse
import com.example.wellbee.data.model.SportRequest
import com.example.wellbee.frontend.components.showTimePicker
import kotlinx.coroutines.launch

@Composable
fun RiwayatScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB))
            .padding(16.dp)
    ) {
        // ===== Header =====
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

            // Tombol Date dropdown (sementara dummy)
            Button(
                onClick = { /* nanti bisa pilih tanggal */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E4DA4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Date", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Tab kategori =====
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

        // ===== Konten berdasarkan tab =====
        when (selectedTab) {
            null -> PilihKategoriScreen()
            "Sport" -> SportRiwayatList()
            "Sleep" -> SleepRiwayatList()
            "Berat Badan" -> BeratBadanRiwayatList()
        }

    }
}
@Composable
fun PilihKategoriScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pilih kategori untuk melihat riwayat",
            color = Color.Gray
        )
    }
}

// =============================
// ======= Sport Section =======
// =============================
@Composable
fun SportRiwayatList() {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }

    var data by remember { mutableStateOf<List<SportHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val prefs = context.getSharedPreferences("wellbee_prefs", Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

    val scope = rememberCoroutineScope()

    // ⬇⬇ STATE EDIT
    var editingItem by remember { mutableStateOf<SportHistory?>(null) }

    LaunchedEffect(Unit) {
        val result = repo.getSportHistory(userId)
        if (result.isSuccess) data = result.getOrNull()!!
        else errorMessage = result.exceptionOrNull()?.message

        isLoading = false
    }

    // ========= LIST ITEM =========
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        data.forEach { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(item.jenisOlahraga, fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                        Text("${item.durasiMenit} menit", color = Color(0xFF00B894))
                    }

                    Row {
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

    // ========= POPUP EDIT =========
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

    // Hitung kalori otomatis
    LaunchedEffect(jenis, durasi) {
        val menit = durasi.toIntOrNull() ?: 0
        if (menit > 0) {
            kalori = hitungKaloriTerbakar(jenis, menit, USER_WEIGHT_KG).toInt().toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),

        title = {
            Text("Edit Data Olahraga",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0E4DA4))
        },

        text = {
            Column {

                OutlinedTextField(
                    value = jenis,
                    onValueChange = { jenis = it },
                    label = { Text("Jenis Olahraga") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durasi,
                    onValueChange = {
                        durasi = it.filter { c -> c.isDigit() }
                    },
                    label = { Text("Durasi (menit)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kalori,
                    onValueChange = {},
                    label = { Text("Kalori Terbakar") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val req = SportRequest(
                        jenisOlahraga = jenis,
                        durasiMenit = durasi.toInt(),
                        kaloriTerbakar = kalori.toInt()
                    )

                    val repo = FisikRepository(context)

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


// =============================
// ======= Sleep Section =======
// =============================
@Composable
fun SleepRiwayatList() {
    val context = LocalContext.current
    val repo = remember { FisikRepository(context) }

    var data by remember { mutableStateOf<List<SleepData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // hanya 1 state untuk editing
    var editingItem by remember { mutableStateOf<SleepData?>(null) }

    // LOAD DATA
    LaunchedEffect(Unit) {
        val result = repo.getSleepHistory()
        if (result.isSuccess) {
            data = result.getOrNull()!!
        } else {
            error = result.exceptionOrNull()?.message
        }
        loading = false
    }

    when {
        loading -> Text("Loading...")
        error != null -> Text("Error: $error", color = Color.Red)
        data.isEmpty() -> Text("Belum ada data tidur.")

        else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            data.forEach { item ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Text("Tidur: ${item.jamTidur}", fontWeight = FontWeight.Bold, color = Color(0xFF0E4DA4))
                            Text("Bangun: ${item.jamBangun}", color = Color(0xFF00B894))
                            Text("Durasi: ${item.durasiTidur} jam")
                            Text("Kualitas: ${item.kualitasTidur}/5")
                        }

                        Row {
                            IconButton(onClick = {
                                editingItem = item  // ← BENER
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF00B894))
                            }

                            IconButton(onClick = {
                                scope.launch {
                                    val result = repo.deleteSleep(item.id)
                                    if (result.isSuccess) {
                                        data = data.filter { it.id != item.id }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFD63031))
                            }
                        }
                    }
                }
            }
        }
    }

    // POPUP EDIT
    if (editingItem != null) {
        EditSleepDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                data = data.map {
                    if (it.id == updated.id) updated else it
                }
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

    // ------ STATE ------
    var jamTidur by remember { mutableStateOf(item.jamTidur) }
    var jamBangun by remember { mutableStateOf(item.jamBangun) }
    var durasiTidur by remember { mutableStateOf(item.durasiTidur.toString()) }
    var kualitas by remember { mutableStateOf(item.kualitasTidur) }

    // TimePicker triggers
    var showPickerTidur by remember { mutableStateOf(false) }
    var showPickerBangun by remember { mutableStateOf(false) }

    fun updateDurasi() {
        durasiTidur = hitungDurasiTidur(jamTidur, jamBangun)
    }

    // ------ TIME PICKERS ------
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
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),

        title = {
            Text(
                "Edit Data Tidur",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0E4DA4)
            )
        },

        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Jam Tidur
                DateTimeField(
                    label = "Jam Tidur",
                    value = jamTidur,
                    onClick = { showPickerTidur = true }
                )

                // Jam Bangun
                DateTimeField(
                    label = "Jam Bangun",
                    value = jamBangun,
                    onClick = { showPickerBangun = true }
                )

                // Durasi Tidur
                OutlinedTextField(
                    value = durasiTidur,
                    onValueChange = {},
                    label = { Text("Durasi Tidur (jam)") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Kualitas Tidur Emoji (sama seperti SleepScreen)
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
                        durasiTidur = durasiTidur.toDoubleOrNull() ?: 0.0,
                        kualitasTidur = kualitas
                    )

                    val repo = FisikRepository(context)
                    val req = SleepRequest(
                        jamTidur = jamTidur,
                        jamBangun = jamBangun,
                        durasiTidur = durasiTidur.toDoubleOrNull() ?: 0.0,
                        kualitasTidur = kualitas
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
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF74B9FF))
            ) {
                Text("Batal", color = Color.White)
            }
        }
    )
}


// =============================
// ==== Berat Badan Section ====
// =============================

@Composable
fun BeratBadanRiwayatList() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Belum ada data berat badan", color = Color.Gray)
    }
}
