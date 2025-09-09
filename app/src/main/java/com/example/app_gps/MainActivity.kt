package com.example.app_gps

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // kiểm tra xem có lưu biển số xe chưa
        val prefs: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val plateNumber = prefs.getString("plate_number", null)

        if (plateNumber.isNullOrEmpty()) {
            // nếu chưa có thì mở PlateNumberActivity để nhập
            val intent = Intent(this, PlateNumberActivity::class.java)
            startActivity(intent)
            finish() // đóng MainActivity để tránh quay ngược lại
        } else {
            // nếu đã có thì load layout chính (ví dụ activity_main.xml)
            setContentView(R.layout.activity_main)

            // TODO: ở đây bạn code phần chính của app (map, GPS, vv...)
        }
    }
}
