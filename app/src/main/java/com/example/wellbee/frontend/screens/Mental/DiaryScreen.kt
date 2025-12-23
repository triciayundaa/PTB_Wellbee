package com.example.wellbee.frontend.screens.Mental

import android.Manifest
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.wellbee.R
import com.example.wellbee.data.JournalRequest
import com.example.wellbee.data.RetrofitClient
import com.example.wellbee.data.local.AppDatabase
import com.example.wellbee.data.local.MentalJournalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(navController: NavHostController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var selectedTrigger by remember { mutableStateOf("Pilih Pemicu") }
    var customTrigger by remember { mutableStateOf(TextFieldValue("")) }
    var diaryText by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    var showSavedPopup by remember { mutableStateOf(false) }


    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var photoPath by remember { mutableStateOf<String?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    var isRecording by remember { mutableStateOf(false) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }

    val triggers = listOf("Tugas", "Pertemanan", "Lainnya")

    fun createImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JOURNAL_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    fun createAudioFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        return File.createTempFile(
            "VOICE_${System.currentTimeMillis()}_",
            ".m4a",
            storageDir
        )
    }

    fun stopRecordingSafely() {
        if (!isRecording) return
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
        } finally {
            recorder = null
            isRecording = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { stopRecordingSafely() }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            photoPath = null
            pendingCameraUri = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            photoPath = null
            pendingCameraUri = null
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val audioFile = createAudioFile(context)
            audioPath = audioFile.absolutePath

            try {
                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(audioPath)
                    prepare()
                    start()
                }
                isRecording = true
            } catch (_: Exception) {
                try {
                    recorder?.release()
                } catch (_: Exception) {}
                recorder = null
                isRecording = false
                audioPath = null
            }
        } else {
            isRecording = false
            audioPath = null
        }
    }

    fun showJournalNotification(context: Context, journalId: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            androidx.core.content.ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, com.example.wellbee.MainActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target_screen", "mental_journal_detail")
            putExtra("journal_id", journalId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            journalId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "mental_channel_id")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Wellbee Journal")
            .setContentText("Diary Anda berhasil disimpan! Tap untuk melihat detail. 📝")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(journalId, builder.build())
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
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
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Mental Health",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.navigate("journal_list") }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Lihat Jurnal",
                            tint = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .animateContentSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Dear Diary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF105490)
                    )
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = Color(0xFF105490)
                    )
                }

                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tanggal Jurnal") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF105490),
                        unfocusedBorderColor = Color(0xFF105490)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFD9F2E6), RoundedCornerShape(20.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTrigger,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF105490)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = null,
                            tint = Color(0xFF105490)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFFD9F2E6))
                ) {
                    triggers.forEach { trigger ->
                        DropdownMenuItem(
                            text = { Text(trigger, color = Color(0xFF105490)) },
                            onClick = {
                                selectedTrigger = trigger
                                expanded = false
                                if (trigger != "Lainnya") customTrigger = TextFieldValue("")
                            }
                        )
                    }
                }

                if (selectedTrigger == "Lainnya") {
                    Spacer(Modifier.height(12.dp))
                    TextField(

                        value = customTrigger,
                        onValueChange = { customTrigger = it },
                        placeholder = { Text("Lainnya...", color = Color(0xFF7A8D92)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD9F2E6), RoundedCornerShape(20.dp)),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color(0xFF105490),
                            fontSize = 16.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFD9F2E6),
                            unfocusedContainerColor = Color(0xFFD9F2E6),
                            cursorColor = Color(0xFF105490)
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                TextField(
                    value = diaryText,
                    onValueChange = { diaryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .shadow(5.dp, RoundedCornerShape(20.dp))
                        .background(Color.White, RoundedCornerShape(20.dp)),
                    placeholder = {
                        Text(
                            "Type here...",
                            color = Color(0xFF7A8D92),
                            textAlign = TextAlign.Start
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF105490),
                        fontSize = 16.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Color(0xFF105490)
                    )
                )

                photoPath?.let {
                    Spacer(Modifier.height(12.dp))
                    Image(
                        painter = rememberAsyncImagePainter(File(it)),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                audioPath?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (isRecording) "Recording..." else "Voice note tersimpan",
                        color = if (isRecording) Color.Red else Color(0xFF105490),
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        stopRecordingSafely()

                        val triggerFinal =
                            if (selectedTrigger == "Lainnya") customTrigger.text else selectedTrigger

                        val currentDate = selectedDate 

                        scope.launch {
                            val newJournal = MentalJournalEntity(
                                userId = 1,
                                triggerLabel = triggerFinal,
                                isiJurnal = diaryText.text,
                                fotoPath = photoPath,
                                audioPath = audioPath,
                                tanggal = currentDate,
                                isSynced = false
                            )

                            var newId: Long = 0
                            withContext(Dispatchers.IO) {
                                val dao = AppDatabase.getInstance(context).mentalDao()
                                newId = dao.insertJournal(newJournal)
                            }

                            showJournalNotification(context, newId.toInt())

                            withContext(Dispatchers.IO) {
                                try {
                                    val apiService = RetrofitClient.getInstance(context)
                                    val request = JournalRequest(
                                        userId = 1,
                                        triggerLabel = triggerFinal,
                                        isiJurnal = diaryText.text,
                                        foto = photoPath,
                                        audio = audioPath,
                                        tanggal = currentDate
                                    )
                                    val response = apiService.postJournal(request)
                                    if (response.isSuccessful) {
                                        Log.d("DiaryScreen", "Sukses upload jurnal ke server")
                                    } else {
                                        Log.e("DiaryScreen", "Gagal upload: ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("DiaryScreen", "Error network: ${e.message}")
                                }
                            }

                            showSavedPopup = true
                            delay(1200)
                            showSavedPopup = false

                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF105490))
                ) {
                    Text("Save", color = Color.White, fontSize = 16.sp)
                }

                Spacer(Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val photoFile = createImageFile(context)
                            photoPath = photoFile.absolutePath

                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                photoFile
                            )
                            pendingCameraUri = uri
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFFD9F2E6))
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, tint = Color(0xFF105490))
                        Spacer(Modifier.width(6.dp))
                        Text("Camera", color = Color(0xFF105490))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            if (!isRecording) {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                stopRecordingSafely()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            if (isRecording) Color(0xFFFFCDD2) else Color(0xFFD9F2E6)
                        )
                    ) {
                        Icon(
                            Icons.Filled.Mic,
                            null,
                            tint = if (isRecording) Color.Red else Color(0xFF105490)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (isRecording) "Stop" else "Voice",
                            color = if (isRecording) Color.Red else Color(0xFF105490)
                        )
                    }
                }

                Spacer(Modifier.height(50.dp))
            }
        }

        if (showSavedPopup) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(Color(0xFFD0E4F2)),
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth(0.75f)
                ) {
                    Column(
                        Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF105490),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Saved",
                            color = Color(0xFF105490),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}