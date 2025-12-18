package com.example.wellbee.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.wellbee.MainActivity
import com.example.wellbee.R // Pastikan import R sesuai package Anda

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        triggerNotification()
        return Result.success()
    }

    private fun triggerNotification() {
        val context = applicationContext
        val channelId = "wellbee_fisik_channel" // ID Khusus Modul Fisik
        val channelName = "Pengingat Aktivitas Fisik"
        val notificationId = 101 // ID Unik untuk notifikasi ini

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Buat Notification Channel (Wajib untuk Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi harian untuk mencatat olahraga dan tidur"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Intent agar saat notifikasi diklik, aplikasi terbuka ke MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        // 3. Desain Notifikasi
        val notification = NotificationCompat.Builder(context, channelId)
            // Gunakan icon default Android jika belum ada logo.
            // Nanti ganti R.drawable.ic_dialog_info dengan logo Wellbee Anda.
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sudah Olahraga Hari Ini? 🏃‍♂️")
            .setContentText("Jangan lupa catat aktivitas fisik & jam tidurmu hari ini di Wellbee!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Hilang saat diklik
            .build()

        // 4. Tampilkan
        notificationManager.notify(notificationId, notification)
    }
}