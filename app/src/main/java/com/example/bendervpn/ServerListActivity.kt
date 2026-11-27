package com.example.bendervpn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ServerListActivity : AppCompatActivity() {

    private data class Place(
        val key: String,
        val title: String,
        val ping: Int,
        val pro: Boolean
    )

    private val places = listOf(
        Place("GERMANY", "Германия", 23, false),
        Place("FRANCE", "Франция", 29, false),
        Place("RUSSIA", "Россия", 19, false),
        Place("ENGLAND", "Англия", 41, true),
        Place("NETHERLANDS", "Нидерланды", 26, true)
    )

    private val handler = Handler(Looper.getMainLooper())
    private val pingViews = mutableListOf<TextView>()
    private val tick = object : Runnable {
        override fun run() {
            val now = System.currentTimeMillis() / 1000L
            pingViews.forEachIndexed { index, view ->
                val base = places[index].ping
                val live = (base + ((now + index) % 5) - 2).toInt().coerceAtLeast(8)
                view.text = "$live ms"
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_list)
        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }

        val prefs = getSharedPreferences("vpn", MODE_PRIVATE)
        val selected = prefs.getString("server", "GERMANY")
        val container = findViewById<LinearLayout>(R.id.serverContainer)
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()
        val logged = prefs.getBoolean("logged_in", false)
        val hasPro = !prefs.getString("wg_key", "").isNullOrBlank()

        places.forEach { place ->
            val chosen = place.key == selected
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER_VERTICAL
            row.setPadding(dp(14), dp(14), dp(14), dp(14))
            row.setBackgroundColor(if (chosen) 0xFF1C1428.toInt() else 0xFF1A1A1A.toInt())
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(10)
            row.layoutParams = params

            val flag = FlagView(this)
            flag.code = when (place.key) {
                "FRANCE" -> "FR"
                "ENGLAND" -> "GB"
                "NETHERLANDS" -> "NL"
                "RUSSIA" -> "RU"
                else -> "DE"
            }
            flag.chosen = chosen
            val flagParams = LinearLayout.LayoutParams(dp(46), dp(32))
            flagParams.marginEnd = dp(14)
            flag.layoutParams = flagParams

            val title = TextView(this)
            title.text = place.title
            title.textSize = 16f
            title.setTextColor(if (chosen) 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
            title.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            row.addView(flag)
            row.addView(title)

            if (place.pro) {
                val crown = CrownView(this)
                val crownParams = LinearLayout.LayoutParams(dp(22), dp(22))
                crownParams.marginEnd = dp(10)
                crown.layoutParams = crownParams
                row.addView(crown)
            }

            val pingView = TextView(this)
            pingView.text = "${place.ping} ms"
            pingView.textSize = 14f
            pingView.setTextColor(0xFF3DDC84.toInt())
            row.addView(pingView)
            pingViews.add(pingView)

            row.setOnClickListener {
                if (place.pro && (!logged || !hasPro)) {
                    val needAccount = !logged
                    AlertDialog.Builder(this)
                        .setTitle("Премиум")
                        .setMessage(if (needAccount) "Нужен аккаунт и подписка" else "Нужна подписка")
                        .setNegativeButton("Закрыть", null)
                        .setPositiveButton(if (needAccount) "Войти" else "Ок", null)
                        .show()
                    return@setOnClickListener
                }
                prefs.edit()
                    .putString("server", place.key)
                    .putString("ping", "${place.ping} ms")
                    .apply()
                Toast.makeText(this, place.title, Toast.LENGTH_SHORT).show()
                finish()
            }
            container.addView(row)
        }
        handler.post(tick)
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        super.onDestroy()
    }
}
