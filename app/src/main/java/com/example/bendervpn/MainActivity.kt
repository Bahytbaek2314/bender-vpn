package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MainActivity : AppCompatActivity() {
    private var isRunning = false
    private lateinit var drawer: DrawerLayout
    private lateinit var vpnButton: ImageView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawerLayout)
        vpnButton = findViewById(R.id.vpnButton)
        val menuButton = findViewById<android.widget.Button>(R.id.menuButton)
        val serverCard = findViewById<LinearLayout>(R.id.serverCard)

        menuButton.setOnClickListener { drawer.openDrawer(Gravity.LEFT) }

        serverCard.setOnClickListener {
            startActivity(Intent(this, ServerListActivity::class.java))
        }

        findViewById<TextView>(R.id.importKeyBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            showImportDialog()
        }
        findViewById<TextView>(R.id.settingsBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<TextView>(R.id.logoutBtn).setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT)
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show()
        }

        updateKeyStatus()
        loadGif(R.drawable.loading)

        vpnButton.setOnClickListener {
            if (!isRunning) {
                val intent = VpnService.prepare(this)
                if (intent != null) startActivityForResult(intent, 1)
                else startConnecting()
            } else {
                stopVpn()
            }
        }
    }

    private fun loadGif(resId: Int) {
        Glide.with(this)
            .asGif()
            .load(resId)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(vpnButton)
    }

    private fun startConnecting() {
        loadGif(R.drawable.loading)
        findViewById<TextView>(R.id.statusText).text = "Подключение..."
        handler.postDelayed({ startVpn() }, 2000)
    }

    private fun updateKeyStatus() {
        val key = getSharedPreferences("vpn", MODE_PRIVATE).getString("key", null)
        val importBtn = findViewById<TextView>(R.id.importKeyBtn)
        importBtn.text = if (key.isNullOrEmpty()) "Импорт ключа" else "Ключ импортирован ✓"
    }

    private fun showImportDialog() {
        val input = EditText(this)
        input.hint = "Вставьте ключ из Telegram"
        AlertDialog.Builder(this)
            .setTitle("Импорт ключа")
            .setView(input)
            .setPositiveButton("Сохранить") { _, _ ->
                val key = input.text.toString().trim()
                if (key.isNotEmpty()) {
                    getSharedPreferences("vpn", MODE_PRIVATE).edit().putString("key", key).apply()
                    updateKeyStatus()
                    Toast.makeText(this, "Ключ сохранён", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun startVpn() {
        startService(Intent(this, SimpleVpnService::class.java))
        isRunning = true
        loadGif(R.drawable.success)
        findViewById<TextView>(R.id.statusText).text = "Подключено"
        Toast.makeText(this, "VPN запущен", Toast.LENGTH_SHORT).show()
    }

    private fun stopVpn() {
        val intent = Intent(this, SimpleVpnService::class.java)
        intent.action = "STOP"
        startService(intent)
        isRunning = false
        loadGif(R.drawable.loading)
        findViewById<TextView>(R.id.statusText).text = "VPN отключен"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) startConnecting()
    }

    override fun onResume() {
        super.onResume()
        updateKeyStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
