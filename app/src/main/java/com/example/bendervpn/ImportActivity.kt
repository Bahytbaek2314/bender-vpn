package com.example.bendervpn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ImportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        val prefs = getSharedPreferences("vpn", MODE_PRIVATE)
        val input = findViewById<EditText>(R.id.keyInput)
        input.setText(prefs.getString("wg_key", ""))

        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }
        findViewById<Button>(R.id.saveKey).setOnClickListener {
            val key = input.text.toString().trim()
            if (key.isEmpty()) {
                Toast.makeText(this, "Вставь ключ", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putString("wg_key", key).apply()
                Toast.makeText(this, "Ключ сохранён", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
