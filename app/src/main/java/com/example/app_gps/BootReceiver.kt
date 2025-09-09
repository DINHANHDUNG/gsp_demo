package com.example.app_gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Khi hệ thống khởi động xong, khởi động LocationService
            val serviceIntent = Intent(context, LocationService::class.java)
            // Vì không có Activity nào để gọi, ta phải dùng startForegroundService()
            // và cung cấp context từ BroadcastReceiver
            ContextCompat.startForegroundService(context!!, serviceIntent)
        }
    }
}