package com.example.app_gps

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 1
    }

    var plateNumber = ""
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val lat = location.latitude
                    val lng = location.longitude

                    Log.d("LocationService", "Lat: $lat, Lng: $lng")

                    // Gửi lên Firebase
                    val carPlate = "29A-12345" // TODO: lấy từ SharedPreferences
                    val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    plateNumber = prefs.getString("plate_number", null) ?: ""

                    val data = mapOf("lat" to lat, "lng" to lng, "car_plate" to plateNumber, "time" to System.currentTimeMillis())
                    FirebaseDatabase.getInstance().reference
                        .child("vehicles")
                        .child(getIdDevice())
                        .setValue(data)
                }
            }
        }

//        requestLocationUpdates()
    }

    // Phương thức này được gọi mỗi khi có yêu cầu bắt đầu service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Tạo và hiển thị thông báo để nâng service lên foreground
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Bắt đầu yêu cầu cập nhật vị trí
        requestLocationUpdates()

        // Trả về START_STICKY để dịch vụ tự khởi động lại nếu bị Android tắt
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Dịch vụ định vị"
            val descriptionText = "Dịch vụ đang chạy ngầm để theo dõi vị trí của xe."
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Dịch vụ theo dõi vị trí")
            .setContentText("Ứng dụng đang hoạt động ở chế độ nền")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Thay bằng icon của bạn
            .setContentIntent(pendingIntent)
            .build()
    }


    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // 60 giây = 1 phút
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Không có quyền → dừng, tránh crash
            return
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Dừng cập nhật vị trí khi service bị hủy
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationService", "Service is being destroyed and location updates are stopped.")
    }

    private fun getIdDevice() : String{
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
