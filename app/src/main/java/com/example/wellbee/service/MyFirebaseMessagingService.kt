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
        // Ambil data payload (ID Artikel) yang dikirim dari server/Firebase
        val articleId = remoteMessage.data["articleId"]

        // Ambil isi teks notifikasi
        val title = remoteMessage.notification?.title ?: "Artikel Baru!"
        val body = remoteMessage.notification?.body ?: "Cek informasi kesehatan terbaru di Wellbee."

        showNotification(title, body, articleId)
    }

    private fun showNotification(title: String, message: String, articleId: String?) {
        val channelId = "wellbee_notifications"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat Notification Channel untuk Android 8.0 ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Wellbee Updates", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        // Setup Intent: Apa yang terjadi jika notifikasi diklik
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Kirim articleId ke MainActivity agar bisa langsung membuka artikel tersebut
            putExtra("articleId", articleId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Bangun notifikasi
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Gunakan icon aplikasi Anda
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}