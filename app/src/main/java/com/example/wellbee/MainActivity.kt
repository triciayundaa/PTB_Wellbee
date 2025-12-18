package com.example.wellbee

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.wellbee.frontend.navigation.NavGraph
import com.example.wellbee.ui.theme.WellbeeTheme
import com.example.wellbee.worker.ReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

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

        // 2. Cek Izin saat aplikasi baru dibuka
        checkNotificationPermission()

        // 3. Jadwalkan Notifikasi Harian (Jam 20:00)
        setupDailyReminder()

        // 4. Setup UI (Navigasi)
        setContent {
            WellbeeTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }

    // --- FUNGSI BANTUAN (LOGIKA FATHIYA) ---

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