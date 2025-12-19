package com.example.wellbee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.wellbee.frontend.navigation.NavGraph
import com.example.wellbee.ui.theme.WellbeeTheme
import com.example.wellbee.worker.ReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // 1. Siapkan Launcher untuk minta izin Notifikasi (Wajib untuk Android 13/Tiramisu ke atas)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Jika user klik "Allow", langsung jadwalkan notifikasi
            setupDailyReminder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("LOG_TEST: Aplikasi Wellbee Running!")

        FirebaseMessaging.getInstance().subscribeToTopic("new_articles")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Gunakan println agar sama dengan LOG_TEST
                    println("LOG_TEST: FCM Sukses subscribe ke topik new_articles")
                } else {
                    println("LOG_TEST: FCM Gagal subscribe: ${task.exception?.message}")
                }
            }

        // 2. Cek Izin dan Jadwalkan Notifikasi
        checkNotificationPermission()
        setupDailyReminder()

        setContent {
            WellbeeTheme {
                val navController = rememberNavController()

                // --- 🔹 LOGIKA NAVIGASI NOTIFIKASI 🔹 ---
                LaunchedEffect(intent) {
                    val articleId = intent.getStringExtra("articleId")
                    if (!articleId.isNullOrBlank()) {
                        // Navigasi ke rute global yang ada di NavGraph
                        navController.navigate("article_detail/$articleId?source=public")
                    }
                }

                NavGraph(navController = navController)
            }
        }
    }

    // --- FUNGSI BANTUAN ---

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Munculkan popup minta izin
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update intent lama agar LaunchedEffect bisa menangkap articleId baru
    }

    private fun setupDailyReminder() {
        val workManager = WorkManager.getInstance(applicationContext)

        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()

        // --- ATUR KE JAM 20:00 (8 MALAM) ---
        dueDate.set(Calendar.HOUR_OF_DAY, 20)
        dueDate.set(Calendar.MINUTE, 0)
        dueDate.set(Calendar.SECOND, 0)

        // Jika jam 20:00 sudah lewat, jadwalkan besok
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag("fisik_reminder")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyFisikReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}