package com.example.app_gps

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.ComponentName
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.provider.Settings

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST = 100
    var plateNumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndRequestPermission()
        // Gọi hàm để hiển thị hộp thoại
        showAutoStartDialog()

        // Kiểm tra và xử lý biển số xe từ SharedPreferences
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        plateNumber = prefs.getString("plate_number", null) ?: ""
        if (plateNumber.isEmpty()) {
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
        serviceIntent.putExtra("plate_number",plateNumber)
//        startService(serviceIntent)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun showAutoStartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cần bật tính năng tự khởi chạy")
            .setMessage("Để ứng dụng hoạt động ổn định khi khởi động lại, vui lòng bật 'Tự khởi chạy' cho ứng dụng trong phần Cài đặt.")
            .setPositiveButton("Đi đến Cài đặt") { _, _ ->
                // Khởi chạy màn hình cài đặt tự khởi động (tùy thuộc vào nhà sản xuất)
                val intent = Intent()
                val manufacturer = Build.MANUFACTURER
                if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                    intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
                } else if ("huawei".equals(manufacturer, ignoreCase = true) || "honor".equals(manufacturer, ignoreCase = true)) {
                    intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
                }
                // Bạn có thể thêm các nhà sản xuất khác ở đây

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // Trường hợp Intent thất bại, chuyển đến màn hình thông tin ứng dụng chung
                    val appInfoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    appInfoIntent.data = Uri.fromParts("package", packageName, null)
                    startActivity(appInfoIntent)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
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
