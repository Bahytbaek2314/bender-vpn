package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {
    private var isRunning = false
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawerLayout)
        val menuButton = findViewById<Button>(R.id.menuButton)
        val vpnButton = findViewById<Button>(R.id.vpnButton)
        val serverCard = findViewById<LinearLayout>(R.id.serverCard)

        menuButton.setOnClickListener {
            drawer.openDrawer(Gravity.LEFT)
        }

        serverCard.setOnClickListener {
            startActivity(Intent(this, ServerListActivity::class.java))
        }

        findViewById<TextView>(R.id.importKeyBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            Toast.makeText(this, "Импорт ключа", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.serversBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            startActivity(Intent(this, ServerListActivity::class.java))
        }
        findViewById<TextView>(R.id.settingsBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.logoutBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show()
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
        findViewById<Button>(R.id.vpnButton).text = "Отключить"
        Toast.makeText(this, "VPN запущен", Toast.LENGTH_SHORT).show()
    }

    private fun stopVpn() {
        val intent = Intent(this, SimpleVpnService::class.java)
        intent.action = "STOP"
        startService(intent)
        isRunning = false
        findViewById<TextView>(R.id.statusText).text = "VPN отключен"
        findViewById<Button>(R.id.vpnButton).text = "Подключиться"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) startVpn()
    }
}
