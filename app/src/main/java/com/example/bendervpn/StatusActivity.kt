package com.example.bendervpn

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }

        val prefs = getSharedPreferences("vpn", MODE_PRIVATE)
        val name = prefs.getString("server", "GERMANY")
        val ping = prefs.getString("ping", "23 ms")
        findViewById<TextView>(R.id.serverValue).text = "$name  •  $ping"
        findViewById<TextView>(R.id.linkValue).text = prefs.getString("link_state", "Отключено")
        val key = prefs.getString("wg_key", "")
        findViewById<TextView>(R.id.keyValue).text = if (key.isNullOrBlank()) "Не добавлен" else "Добавлен"
    }
}
