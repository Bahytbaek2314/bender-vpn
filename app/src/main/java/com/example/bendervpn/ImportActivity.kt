package com.example.bendervpn

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
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

        val density = resources.displayMetrics.density
        input.background = pill(0xFF161616.toInt(), 16f * density)
        val save = findViewById<TextView>(R.id.saveKey)
        save.background = pill(0xFFB388FF.toInt(), 16f * density)

        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }
        save.setOnClickListener {
            val key = input.text.toString().trim()
            if (key.isEmpty()) {
                Toast.makeText(this, "Вставь ключ", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putString("wg_key", key).apply()
                Toast.makeText(this, "Pro включён", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun pill(color: Int, radius: Float): GradientDrawable {
        val shape = GradientDrawable()
        shape.setColor(color)
        shape.cornerRadius = radius
        return shape
    }
}
