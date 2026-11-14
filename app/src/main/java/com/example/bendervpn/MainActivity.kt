package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menuButton = findViewById<Button>(R.id.menuButton)
        val vpnButton = findViewById<Button>(R.id.vpnButton)
        val statusText = findViewById<TextView>(R.id.statusText)
        val serverCard = findViewById<LinearLayout>(R.id.serverCard)
        val addTraffic = findViewById<Button>(R.id.addTrafficButton)

        menuButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        serverCard.setOnClickListener {
            startActivity(Intent(this, ServerListActivity::class.java))
        }

        addTraffic.setOnClickListener {
            Toast.makeText(this, "Премиум пока в разработке", Toast.LENGTH_SHORT).show()
        }

        vpnButton.setOnClickListener {
            if (!isRunning) {
                val intent = VpnService.prepare(this)
                if (intent != null) startActivityForResult(intent, 1)
                else startVpn()
            } else {
                stopVpn()
            }
        }
    }

    private fun startVpn() {
        startService(Intent(this, SimpleVpnService::class.java))
        isRunning = true
        findViewById<TextView>(R.id.statusText).text = "Подключено"
        findViewById<Button>(R.id.vpnButton).text = "⏻"
        Toast.makeText(this, "VPN запущен", Toast.LENGTH_SHORT).show()
    }

    private fun stopVpn() {
        val intent = Intent(this, SimpleVpnService::class.java)
        intent.action = "STOP"
        startService(intent)
        isRunning = false
        findViewById<TextView>(R.id.statusText).text = "VPN отключен"
        findViewById<Button>(R.id.vpnButton).text = "⏻"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) startVpn()
    }
}
