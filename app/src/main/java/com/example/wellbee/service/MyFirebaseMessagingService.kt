package com.example.wellbee.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.wellbee.MainActivity
import com.example.wellbee.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Dipanggil saat pesan FCM diterima
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 1. Ambil Data Payload
        val articleId = remoteMessage.data["articleId"]        // Punya Teman
        val targetScreen = remoteMessage.data["target_screen"] // PUNYA KAMU (BARU)

        // 2. Ambil Teks Notifikasi
        val title = remoteMessage.notification?.title ?: "WellBee Info"
        val body = remoteMessage.notification?.body ?: "Cek aktivitas terbaru kamu."

        // 3. Tampilkan Notifikasi
        showNotification(title, body, articleId, targetScreen)
    }

    private fun showNotification(title: String, message: String, articleId: String?, targetScreen: String?) {
        val channelId = "wellbee_channel_id"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Channel (Wajib untuk Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Wellbee Notification", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        // Setup Intent ke MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // LOGIKA GABUNGAN: Masukkan data sesuai yang dikirim backend
            if (articleId != null) {
                putExtra("articleId", articleId)
            }
            if (targetScreen != null) {
                putExtra("target_screen", targetScreen) // INI KUNCI NAVIGASI KAMU
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Bangun Notifikasi
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo) // Pastikan icon ini ada
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}