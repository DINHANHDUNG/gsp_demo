package com.example.app_gps

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlateNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate_number)

        val editTextPlate = findViewById<EditText>(R.id.editTextPlate)
        val btnSavePlate = findViewById<Button>(R.id.btnSavePlate)

        btnSavePlate.setOnClickListener {
            val plateNumber = editTextPlate.text.toString().trim()

            if (plateNumber.isNotEmpty()) {
                // Lưu vào SharedPreferences
                val sharedPreferences: SharedPreferences =
                    getSharedPreferences("AppPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("plate_number", plateNumber)
                editor.apply()

                Toast.makeText(this, "Lưu biển số thành công!", Toast.LENGTH_SHORT).show()

                // Chuyển về MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Vui lòng nhập biển số xe!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
