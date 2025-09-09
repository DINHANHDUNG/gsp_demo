package com.example.app_gps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermission()

        // Kiểm tra và xử lý biển số xe từ SharedPreferences
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val plateNumber = prefs.getString("plate_number", null)

        if (plateNumber.isNullOrEmpty()) {
            // Nếu chưa có biển số xe, mở activity nhập biển số
            val intent = Intent(this, PlateNumberActivity::class.java)
            startActivity(intent)
            finish() // Đóng MainActivity để tránh quay lại
        } else {
            // Nếu có biển số xe, thực hiện phần chính của ứng dụng (ví dụ: GPS, Map)
            // TODO: Ở đây bạn có thể đặt các tính năng như GPS, bản đồ
        }
    }

    private fun checkAndRequestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Yêu cầu quyền truy cập vị trí
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            // Nếu quyền đã được cấp, khởi động dịch vụ vị trí
            startLocationService()
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Nếu quyền được cấp, khởi động dịch vụ vị trí
                startLocationService()
            } else {
                // Nếu quyền bị từ chối, thông báo yêu cầu quyền
                Toast.makeText(this, "Bạn cần cấp quyền GPS để tiếp tục", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
