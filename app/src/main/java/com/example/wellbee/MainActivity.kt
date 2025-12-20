package com.example.wellbee

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.navigation.NavGraph
import com.example.wellbee.ui.theme.WellbeeTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // [BARU] Launcher untuk meminta izin notifikasi secara otomatis di Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM", "Izin notifikasi diberikan oleh user")
        } else {
            Log.w("FCM", "Izin notifikasi ditolak oleh user")
        }
    }

    private var intentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentState = intent

        createNotificationChannel()
        createMentalNotificationChannel()

        // 1. [BARU] Logika Request Permission (Wajib untuk Android 13/Tiramisu ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // 2. [BARU] Subscribe ke Topic 'new_articles'
        // Agar HP ini bisa menerima notifikasi broadcast artikel dari backend
        FirebaseMessaging.getInstance().subscribeToTopic("new_articles")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Sukses subscribe ke topic: new_articles")
                } else {
                    Log.e("FCM", "Gagal subscribe ke topic")
                }
            }

        // Ambil FCM Token untuk notifikasi personal (Olahraga/Fisik)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Token HP: $token") // Token ini yang masuk ke log VS Code anda
        })

        setContent {
            WellbeeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    LaunchedEffect(intentState) {
                        intentState?.let { currentIntent ->
                            val targetScreen = currentIntent.getStringExtra("target_screen")

                            // 1. Modul Fisik
                            if (targetScreen == "physical_health") {
                                navController.navigate("global_sport_screen") {
                                    launchSingleTop = true
                                }
                            }

                            // 2. Modul Mental - Detail Diary
                            else if (targetScreen == "mental_journal_detail") {
                                val journalId = currentIntent.getIntExtra("journal_id", -1)
                                if (journalId != -1) {
                                    navController.navigate("detail_diary/$journalId") {
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate("journal_list")
                                }
                            }

                            // 3. Modul Mental - Journal List
                            else if (targetScreen == "mental_journal_list") {
                                navController.navigate("journal_list") {
                                    launchSingleTop = true
                                }
                            }

                            // 4. [BARU] Modul Edukasi - Detail Artikel
                            else if (targetScreen == "education_detail") {
                                val articleId = currentIntent.getStringExtra("articleId")
                                if (articleId != null) {
                                    navController.navigate("article_detail/$articleId?source=public") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        }
                    }

                    NavGraph(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentState = intent
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Wellbee Notification"
            val descriptionText = "Channel for general notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("wellbee_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createMentalNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Mental Health Diary"
            val descriptionText = "Notifikasi saat jurnal disimpan"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("mental_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
