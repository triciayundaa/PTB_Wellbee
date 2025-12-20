package com.example.wellbee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.wellbee.frontend.navigation.NavGraph
import com.example.wellbee.ui.theme.WellbeeTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // [MODIFIKASI] State untuk menyimpan Intent terbaru
    // Ini memungkinkan LaunchedEffect di Compose merespons saat ada intent baru dari notifikasi
    private var intentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inisialisasi state dengan intent awal
        intentState = intent

        createNotificationChannel()
        createMentalNotificationChannel() // Channel baru untuk Mental Health

        // (Opsional) Ambil FCM Token jika nanti pakai Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            // Log.d("FCM", "Token: $token")
        })

        setContent {
            WellbeeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // [MODIFIKASI] Pantau 'intentState' bukannya 'intent' biasa
                    LaunchedEffect(intentState) {
                        intentState?.let { currentIntent ->
                            val targetScreen = currentIntent.getStringExtra("target_screen")

                            // 1. Modul Fisik (sudah ada)
                            if (targetScreen == "physical_health") {
                                // SEBELUMNYA (SALAH): "physical_health_screen"
                                // SEKARANG (BENAR): Gunakan nama dari NavGraph kamu
                                navController.navigate("global_sport_screen") {
                                    launchSingleTop = true
                                }
                            }
                            
                            // 2. Modul Mental - Detail Diary (BARU)
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
                            
                            // 3. Modul Mental - Journal List (OPSIONAL)
                            else if (targetScreen == "mental_journal_list") {
                                navController.navigate("journal_list") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                    NavGraph(navController = navController)
                }
            }
        }
    }

    // [MODIFIKASI] Tangkap Intent baru saat aplikasi sedang berjalan
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update intent standar Android
        intentState = intent // Update state Compose agar LaunchedEffect jalan lagi
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