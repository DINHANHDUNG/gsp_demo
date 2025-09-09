package com.example.app_gps

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
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

        requestLocationUpdates()
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

    private fun getIdDevice() : String{
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
