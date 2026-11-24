package com.example.bendervpn

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ServerListActivity : AppCompatActivity() {

    private val servers = listOf(
        "GERMANY" to "23 ms",
        "FRANCE" to "29 ms",
        "ENGLAND" to "41 ms",
        "NETHERLANDS" to "26 ms",
        "RUSSIA" to "19 ms"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_list)

        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }

        val selected = getSharedPreferences("vpn", MODE_PRIVATE).getString("server", "GERMANY")
        val container = findViewById<LinearLayout>(R.id.serverContainer)
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()

        servers.forEach { (name, ping) ->
            val chosen = name == selected
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
            flag.code = when (name) {
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
            title.text = name
            title.textSize = 16f
            title.setTextColor(if (chosen) 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
            title.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            row.addView(flag)
            row.addView(title)
            if (chosen) {
                val crown = CrownView(this)
                val crownParams = LinearLayout.LayoutParams(dp(22), dp(22))
                crownParams.marginEnd = dp(10)
                crown.layoutParams = crownParams
                row.addView(crown)
            }

            val pingView = TextView(this)
            pingView.text = ping
            pingView.textSize = 14f
            pingView.setTextColor(0xFF00D4FF.toInt())
            row.addView(pingView)

            row.setOnClickListener {
                setResult(RESULT_OK, Intent().putExtra("server", name).putExtra("ping", ping))
                finish()
            }
            container.addView(row)
        }
    }
}
