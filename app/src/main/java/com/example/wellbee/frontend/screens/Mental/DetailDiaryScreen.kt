package com.example.wellbee.frontend.screens.Mental

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
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
import coil.compose.AsyncImage
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.MentalJournalEntity
import com.example.wellbee.data.local.MentalMoodEntity
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailDiaryScreen(
    navController: NavHostController,
    journalId: Int
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).mentalDao() }
    val scope = rememberCoroutineScope()
    
    var journal by remember { mutableStateOf<MentalJournalEntity?>(null) }
    var mood by remember { mutableStateOf<MentalMoodEntity?>(null) }

    // Edit Mode State
    var isEditing by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf("") }

    // Audio Player States
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
    }
    
    LaunchedEffect(journalId) {
        val j = dao.getJournalDetail(journalId)
        journal = j
        if (j != null) {
            mood = dao.getMoodByDate(j.userId, j.tanggal)
            editedContent = j.isiJurnal // Initialize edit content
        }
    }

    if (journal == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF105490))
        }
        return
    }

    val item = journal!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
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
                    text = if (isEditing) "Edit Journal" else "Journal Detail",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    if (isEditing) {
                        // Cancel Button
                        IconButton(onClick = { 
                            isEditing = false 
                            editedContent = item.isiJurnal // Reset content
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.White)
                        }
                        // Save Button
                        IconButton(onClick = {
                            scope.launch {
                                val updatedJournal = item.copy(isiJurnal = editedContent)
                                dao.updateJournal(updatedJournal)
                                journal = updatedJournal
                                isEditing = false
                            }
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = Color.White)
                        }
                    } else {
                        // Edit Button
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Tanggal
            Text(
                text = item.tanggal,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mood Section
            mood?.let { m ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(text = m.emoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Mood: ${m.moodLabel}", 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF105490),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Intensity: ${m.moodScale}/10", 
                            fontSize = 14.sp, 
                            color = Color(0xFF105490)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Title
            Text(
                text = item.triggerLabel ?: "No Title",
                color = Color(0xFF105490),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Foto
            if (!item.fotoPath.isNullOrEmpty()) {
                val file = File(item.fotoPath)
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Journal Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray, RoundedCornerShape(12.dp)),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Audio Player
            if (!item.audioPath.isNullOrEmpty()) {
                val audioFile = File(item.audioPath)
                if (audioFile.exists()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE0F7FA), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    try {
                                        mediaPlayer?.stop()
                                        mediaPlayer?.reset()
                                        mediaPlayer?.release()
                                    } catch (e: Exception) { e.printStackTrace() }
                                    mediaPlayer = null
                                    isPlaying = false
                                } else {
                                    try {
                                        mediaPlayer = MediaPlayer().apply {
                                            setDataSource(item.audioPath)
                                            prepare()
                                            start()
                                            setOnCompletionListener {
                                                isPlaying = false
                                                it.release()
                                                mediaPlayer = null
                                            }
                                        }
                                        isPlaying = true
                                    } catch (e: Exception) { e.printStackTrace() }
                                }
                            },
                            modifier = Modifier
                                .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Stop" else "Play",
                                tint = Color(0xFF105490),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                             Text(
                                text = if (isPlaying) "Playing Audio..." else "Voice Note",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF105490),
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isPlaying) "Tap to stop" else "Tap play to listen",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Isi Jurnal (Editable)
            if (isEditing) {
                OutlinedTextField(
                    value = editedContent,
                    onValueChange = { editedContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFFAFAFA)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF105490),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Text(
                    text = item.isiJurnal,
                    color = Color.Black,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
            
            // Spacer bottom for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
