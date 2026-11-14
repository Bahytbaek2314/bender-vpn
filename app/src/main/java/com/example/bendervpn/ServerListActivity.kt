package com.example.bendervpn

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ServerListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_list)

        val servers = arrayOf(
            "🇺🇸 United States",
            "🇫🇷 France",
            "🇩🇪 Germany",
            "🇬🇧 United Kingdom",
            "🇳🇱 Netherlands",
            "🇷🇺 Russia",
            "🇸🇬 Singapore",
            "🇯🇵 Japan",
            "🇨🇦 Canada",
            "🇦🇺 Australia"
        )

        val listView = findViewById<ListView>(R.id.serverList)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, servers)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = servers[position]
            Toast.makeText(this, "Выбран: $selected", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
