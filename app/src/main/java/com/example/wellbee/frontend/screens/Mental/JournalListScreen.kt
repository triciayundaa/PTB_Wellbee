package com.example.wellbee.frontend.screens.Mental

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
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
import com.example.wellbee.data.local.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).mentalDao() }
    val scope = rememberCoroutineScope()

    val journals by dao.observeJournalsByUser(userId = 1).collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }
    var selectedFilterDate by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            selectedFilterDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val filteredJournals = remember(journals, searchQuery, selectedFilterDate) {
        journals.filter { journal ->

            val matchesDate = if (selectedFilterDate != null) {
                journal.tanggal == selectedFilterDate
            } else {
                true
            }

            val matchesSearch = if (searchQuery.isNotBlank()) {
                (journal.triggerLabel?.contains(searchQuery, ignoreCase = true) == true) ||
                (journal.isiJurnal.contains(searchQuery, ignoreCase = true)) ||
                (journal.tanggal.contains(searchQuery, ignoreCase = true))
            } else {
                true
            }

            matchesDate && matchesSearch
        }
    }


    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Jurnal") },
            text = { Text("Apakah Anda yakin ingin menghapus jurnal ini? Data yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            dao.deleteJournal(showDeleteDialog!!)
                            showDeleteDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Batal", color = Color(0xFF105490))
                }
            },
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF105490))
                .padding(top = 40.dp, bottom = 20.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Mental Health",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(40.dp))
            }
        }


        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Cari jurnal (judul, isi, tanggal)...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF105490),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Your Journal List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF105490)
                )

                Button(
                    onClick = { 
                        if (selectedFilterDate == null) {
                            datePickerDialog.show() 
                        } else {
                            selectedFilterDate = null
                        }
                    }, 
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFilterDate != null) Color(0xFF105490) else Color(0xFFD9F2E6)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange, 
                        contentDescription = null,
                        tint = if (selectedFilterDate != null) Color.White else Color(0xFF105490),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (selectedFilterDate != null) selectedFilterDate!! else "Filter Date", 
                        color = if (selectedFilterDate != null) Color.White else Color(0xFF105490),
                        fontSize = 12.sp
                    )
                }
            }

            if (filteredJournals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp), 
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (searchQuery.isNotEmpty() || selectedFilterDate != null) "Tidak ditemukan hasil." else "Belum ada jurnal.", 
                        color = Color.Gray
                    )
                }
            } else {
                filteredJournals.forEach { journal ->
                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                navController.navigate("detail_diary/${journal.id}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = Color(0xFF105490),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = journal.triggerLabel ?: "No Title",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF105490)
                                )
                                Text(
                                    text = journal.tanggal,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = if (journal.isiJurnal.length > 30) journal.isiJurnal.take(30) + "..." else journal.isiJurnal,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }

                            IconButton(onClick = { showDeleteDialog = journal.id }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
