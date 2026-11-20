package com.example.bendervpn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var vpnButton: Button
    private lateinit var statusText: TextView
    private lateinit var serverText: TextView
    private lateinit var drawerStatus: TextView

    private var isConnected = false
    private var daysLeft = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        vpnButton = findViewById(R.id.vpnButton)
        statusText = findViewById(R.id.statusText)
        serverText = findViewById(R.id.serverText)
        drawerStatus = findViewById(R.id.drawerStatus)

        updateStatus()

        vpnButton.setOnClickListener {
            if (!isConnected) {
                startConnecting()
            } else {
                disconnect()
            }
        }

        findViewById<TextView>(R.id.importKey).setOnClickListener {
            drawerLayout.closeDrawers()
        }

        findViewById<TextView>(R.id.support).setOnClickListener {
            drawerLayout.closeDrawers()
        }

        findViewById<TextView>(R.id.telegram).setOnClickListener {
            drawerLayout.closeDrawers()
        }
    }

    private fun updateStatus() {
        if (daysLeft > 0) {
            statusText.text = "VIP • $daysLeft дней"
            drawerStatus.text = "СТАТУС: VIP • $daysLeft дней"
        } else {
            statusText.text = "Без подписки"
            drawerStatus.text = "СТАТУС: Без подписки"
        }
    }

    private fun startConnecting() {
        vpnButton.text = "..."
        statusText.text = "Подключение..."

        Handler(Looper.getMainLooper()).postDelayed({
            isConnected = true
            vpnButton.text = "ON"
            statusText.text = "Подключено"
        }, 2000)
    }

    private fun disconnect() {
        isConnected = false
        vpnButton.text = "VPN"
        updateStatus()
    }
}
