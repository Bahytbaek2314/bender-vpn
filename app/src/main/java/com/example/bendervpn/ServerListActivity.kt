package com.example.bendervpn

import android.content.Intent
import android.os.Bundle
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
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(dp(16), dp(18), dp(16), dp(18))
            row.setBackgroundColor(0xFF1A1A1A.toInt())
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(10)
            row.layoutParams = params

            val title = TextView(this)
            title.text = name
            title.textSize = 16f
            title.setTextColor(if (name == selected) 0xFFFF6B00.toInt() else 0xFFFFFFFF.toInt())
            title.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val pingView = TextView(this)
            pingView.text = ping
            pingView.textSize = 14f
            pingView.setTextColor(0xFF00D4FF.toInt())

            row.addView(title)
            row.addView(pingView)
            row.setOnClickListener {
                setResult(RESULT_OK, Intent().putExtra("server", name).putExtra("ping", ping))
                finish()
            }
            container.addView(row)
        }
    }
}
