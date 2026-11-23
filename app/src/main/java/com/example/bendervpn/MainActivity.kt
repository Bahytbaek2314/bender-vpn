package com.example.bendervpn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var vpnButton: PowerButton
    private lateinit var statusText: TextView
    private lateinit var serverText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var connectRunnable: Runnable? = null
    private var state = 0

    private val prefs by lazy { getSharedPreferences("vpn", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        vpnButton = findViewById(R.id.vpnButton)
        statusText = findViewById(R.id.statusText)
        serverText = findViewById(R.id.serverText)

        if (!prefs.contains("server")) {
            prefs.edit().putString("server", "GERMANY").putString("ping", "23 ms").apply()
        }

        showServer()
        paint()

        findViewById<TextView>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        vpnButton.setOnClickListener { onPower() }

        serverText.setOnClickListener {
            startActivityForResult(Intent(this, ServerListActivity::class.java), 42)
        }

        findViewById<TextView>(R.id.drawerStatus).setOnClickListener {
            open(StatusActivity::class.java)
        }
        findViewById<TextView>(R.id.importKey).setOnClickListener {
            open(ImportActivity::class.java)
        }
        findViewById<TextView>(R.id.support).setOnClickListener {
            openInfo("Поддержка", "Сюда позже встанет почта и чат. Сейчас это пустой экран каркаса.")
        }
        findViewById<TextView>(R.id.telegram).setOnClickListener {
            openInfo("Наш Telegram", "Ссылку на канал поставим, когда она будет. Сейчас экран только занимает место в меню.")
        }
        findViewById<TextView>(R.id.settingsButton).setOnClickListener {
            open(SettingsActivity::class.java)
        }
    }

    private fun open(cls: Class<*>) {
        drawerLayout.closeDrawers()
        startActivity(Intent(this, cls))
    }

    private fun openInfo(title: String, body: String) {
        drawerLayout.closeDrawers()
        startActivity(Intent(this, InfoActivity::class.java)
            .putExtra("title", title)
            .putExtra("body", body))
    }

    private fun showServer() {
        val name = prefs.getString("server", "GERMANY")
        val ping = prefs.getString("ping", "23 ms")
        serverText.text = "$name  •  $ping"
    }

    private fun onPower() {
        if (state == 0) {
            state = 1
            statusText.text = "Подключение..."
            statusText.setTextColor(0xFFB388FF.toInt())
            prefs.edit().putString("link_state", "Подключение...").apply()
            vpnButton.playRaw(R.raw.press, false) {
                if (state != 1) return@playRaw
                vpnButton.playRaw(R.raw.loading, true, null)
                val task = Runnable {
                    if (state != 1) return@Runnable
                    state = 2
                    paint()
                }
                connectRunnable = task
                handler.postDelayed(task, 1800)
            }
        } else {
            connectRunnable?.let { handler.removeCallbacks(it) }
            state = 0
            paint()
        }
    }

    private fun paint() {
        val label: String
        val color: Int
        if (state == 2) {
            vpnButton.showStill(R.drawable.vpn_connected)
            label = "Подключено"
            color = 0xFFB388FF.toInt()
        } else {
            vpnButton.showStill(R.drawable.vpn_button)
            label = "Отключено"
            color = 0xFFAAAAAA.toInt()
        }
        statusText.text = label
        statusText.setTextColor(color)
        prefs.edit().putString("link_state", label).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            prefs.edit()
                .putString("server", data.getStringExtra("server"))
                .putString("ping", data.getStringExtra("ping"))
                .apply()
            connectRunnable?.let { handler.removeCallbacks(it) }
            state = 0
            showServer()
            paint()
        }
    }

    override fun onDestroy() {
        connectRunnable?.let { handler.removeCallbacks(it) }
        super.onDestroy()
    }
}