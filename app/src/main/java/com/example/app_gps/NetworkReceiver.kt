package com.example.app_gps

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return
        val capabilities = cm.getNetworkCapabilities(network) ?: return

        val connected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (connected) {
            // Khi có mạng trở lại, gọi retry
            val serviceIntent = Intent(context, LocationService::class.java).apply {
                action = "RETRY_PENDING"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}