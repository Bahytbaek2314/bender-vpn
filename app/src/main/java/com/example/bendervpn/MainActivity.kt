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
    private lateinit var homeServers: android.widget.LinearLayout

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
        homeServers = findViewById(R.id.homeServers)

        if (!prefs.contains("server")) {
            prefs.edit().putString("server", "GERMANY").putString("ping", "23 ms").apply()
        }

        showServer()
        paint()

        findViewById<android.view.View>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        vpnButton.setOnClickListener { onPower() }



        findViewById<android.view.View>(R.id.drawerStatus).setOnClickListener {
            open(StatusActivity::class.java)
        }
        findViewById<android.view.View>(R.id.importKey).setOnClickListener {
            open(ImportActivity::class.java)
        }
        findViewById<android.view.View>(R.id.support).setOnClickListener {
            openInfo("Поддержка", "Сюда позже встанет почта и чат. Сейчас это пустой экран каркаса.")
        }
        findViewById<android.view.View>(R.id.telegram).setOnClickListener {
            openInfo("Наш Telegram", "Ссылку на канал поставим, когда она будет. Сейчас экран только занимает место в меню.")
        }
        findViewById<android.view.View>(R.id.settingsButton).setOnClickListener {
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
        val selected = prefs.getString("server", "GERMANY")
        homeServers.removeAllViews()
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()
        val servers = listOf(
            "GERMANY" to "23 ms",
            "FRANCE" to "29 ms",
            "ENGLAND" to "41 ms",
            "NETHERLANDS" to "26 ms",
            "RUSSIA" to "19 ms"
        )
        servers.forEach { pair ->
            val name = pair.first
            val ping = pair.second
            val chosen = name == selected
            val row = android.widget.LinearLayout(this)
            row.orientation = android.widget.LinearLayout.HORIZONTAL
            row.gravity = android.view.Gravity.CENTER_VERTICAL
            row.setPadding(dp(12), dp(10), dp(12), dp(10))
            row.setBackgroundColor(if (chosen) 0xFF1C1428.toInt() else 0xFF1A1A1A.toInt())
            val params = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(8)
            row.layoutParams = params
            val flag = FlagView(this)
            flag.code = when (name) {
                "FRANCE" -> "FR"
                "ENGLAND" -> "GB"
                "NETHERLANDS" -> "NL"
                "RUSSIA" -> "RU"
                else -> "DE"
            }
            flag.chosen = chosen
            val flagParams = android.widget.LinearLayout.LayoutParams(dp(36), dp(24))
            flagParams.marginEnd = dp(10)
            flag.layoutParams = flagParams
            val title = TextView(this)
            title.text = name
            title.textSize = 14f
            title.setTextColor(if (chosen) 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
            title.layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            row.addView(flag)
            row.addView(title)
            val crown = CrownView(this)
            val crownParams = android.widget.LinearLayout.LayoutParams(dp(16), dp(16))
            crownParams.marginEnd = dp(8)
            crown.layoutParams = crownParams
            row.addView(crown)
            val pingView = TextView(this)
            pingView.text = ping
            pingView.textSize = 13f
            pingView.setTextColor(0xFF00D4FF.toInt())
            row.addView(pingView)
            row.setOnClickListener {
                prefs.edit().putString("server", name).putString("ping", ping).apply()
                connectRunnable?.let { handler.removeCallbacks(it) }
                state = 0
                showServer()
                paint()
            }
            homeServers.addView(row)
        }
    }

    private fun onPower() {
        if (state == 0) {
            state = 1
            paint()
            val task = Runnable {
                if (state != 1) return@Runnable
                state = 2
                paint()
            }
            connectRunnable = task
            handler.postDelayed(task, 1600)
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
            vpnButton.setMode(2)
            label = "Подключено"
            color = 0xFFB388FF.toInt()
        } else if (state == 1) {
            vpnButton.setMode(1)
            label = "Подключение..."
            color = 0xFFB388FF.toInt()
        } else {
            vpnButton.setMode(0)
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
