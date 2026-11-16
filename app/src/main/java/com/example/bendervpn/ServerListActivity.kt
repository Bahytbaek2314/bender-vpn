package com.example.bendervpn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ServerListActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var isLoggedIn = false

    private val premiumServers = listOf(
        "🇺🇸 United States",
        "🇩🇪 Germany",
        "🇫🇷 France",
        "🇬🇧 United Kingdom",
        "🇳🇱 Netherlands"
    )

    private val freeServers = listOf(
        "🇺🇸 United States",
        "🇩🇪 Germany",
        "🇳🇱 Netherlands"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_list)

        findViewById<View>(R.id.backButton).setOnClickListener { finish() }

        val premiumContainer = findViewById<LinearLayout>(R.id.premiumContainer)
        val freeContainer = findViewById<LinearLayout>(R.id.freeContainer)

        premiumServers.forEach { name ->
            premiumContainer.addView(createServerRow(name, true))
        }
        freeServers.forEach { name ->
            freeContainer.addView(createServerRow(name, false))
        }

        startPingUpdates()
    }

    private fun createServerRow(name: String, isPremium: Boolean): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 12, 0, 12)
            setBackgroundColor(0xFF1A1A1A.toInt())
        }

        val flag = TextView(this).apply {
            text = name.take(2)
            textSize = 20f
        }

        val title = TextView(this).apply {
            text = name.drop(2).trim()
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12
            }
        }

        val ping = TextView(this).apply {
            text = "${(30..80).random()} ms"
            textSize = 14f
            setTextColor(0xFF00D4FF.toInt())
            tag = "ping"
        }

        row.addView(flag)
        row.addView(title)
        if (isPremium) {
            val badge = TextView(this).apply {
                text = "💎"
                textSize = 16f
                setPadding(8, 0, 8, 0)
            }
            row.addView(badge)
        }
        row.addView(ping)

        row.setOnClickListener {
            if (isPremium && !isLoggedIn) {
                AlertDialog.Builder(this)
                    .setMessage("Войдите в аккаунт, чтобы продолжить")
                    .setPositiveButton("Войти") { _, _ ->
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    .show()
            } else {
                val result = Intent()
                result.putExtra("server", name)
                setResult(RESULT_OK, result)
                finish()
            }
        }

        return row
    }

    private fun startPingUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updatePings(findViewById(R.id.premiumContainer))
                updatePings(findViewById(R.id.freeContainer))
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun updatePings(container: LinearLayout) {
        for (i in 0 until container.childCount) {
            val row = container.getChildAt(i) as LinearLayout
            val ping = row.findViewWithTag<TextView>("ping")
            ping?.text = "${(30..80).random()} ms"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
