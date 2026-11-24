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
    private lateinit var pingText: TextView
    private lateinit var signalBars: SignalBars
    private lateinit var homeFlag: FlagView

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
        pingText = findViewById(R.id.pingText)
        signalBars = findViewById(R.id.signalBars)
        homeFlag = findViewById(R.id.homeFlag)
        val serverRow = findViewById<android.widget.LinearLayout>(R.id.serverRow)
        val shape = android.graphics.drawable.GradientDrawable()
        shape.setColor(0xFF161616.toInt())
        shape.cornerRadius = 18f * resources.displayMetrics.density
        serverRow.background = shape
        serverRow.setOnClickListener {
            startActivityForResult(Intent(this, ServerListActivity::class.java), 42)
        }

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
        val name = prefs.getString("server", "GERMANY")
        val ping = prefs.getString("ping", "23 ms")
        serverText.text = name
        pingText.text = ping
        val digits = ping?.filter { it.isDigit() } ?: ""
        val ms = if (digits.isEmpty()) 99 else digits.toInt()
        signalBars.pingMs = ms
        pingText.setTextColor(if (ms <= 80) 0xFF3DDC84.toInt() else 0xFFFF4D4D.toInt())
        homeFlag.code = when (name) {
            "FRANCE" -> "FR"
            "ENGLAND" -> "GB"
            "NETHERLANDS" -> "NL"
            "RUSSIA" -> "RU"
            else -> "DE"
        }
        homeFlag.invalidate()
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
