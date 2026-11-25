package com.example.bendervpn

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }

        val density = resources.displayMetrics.density
        listOf(R.id.cardSub, R.id.cardServer, R.id.cardLink, R.id.cardKey).forEach { id ->
            findViewById<LinearLayout>(id).background = card(0xFF161616.toInt(), 18f * density)
        }

        val prefs = getSharedPreferences("vpn", MODE_PRIVATE)
        val code = prefs.getString("server", "GERMANY")
        val ping = prefs.getString("ping", "23 ms")
        val names = mapOf(
            "GERMANY" to "Германия",
            "FRANCE" to "Франция",
            "RUSSIA" to "Россия",
            "ENGLAND" to "Англия",
            "NETHERLANDS" to "Нидерланды"
        )
        val title = names[code] ?: "Германия"
        val pro = !prefs.getString("wg_key", "").isNullOrBlank()
        val sub = findViewById<TextView>(R.id.subValue)
        sub.text = if (pro) "Pro активен" else "Без подписки"
        sub.setTextColor(if (pro) 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
        findViewById<TextView>(R.id.serverValue).text = "$title  •  $ping"
        val link = findViewById<TextView>(R.id.linkValue)
        val state = prefs.getString("link_state", "Отключено")
        link.text = state
        link.setTextColor(if (state == "Подключено") 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
        val key = prefs.getString("wg_key", "")
        val keyView = findViewById<TextView>(R.id.keyValue)
        keyView.text = if (key.isNullOrBlank()) "Не добавлен" else "Добавлен"
        keyView.setTextColor(if (key.isNullOrBlank()) 0xFFFFFFFF.toInt() else 0xFF3DDC84.toInt())
    }

    private fun card(color: Int, radius: Float): GradientDrawable {
        val shape = GradientDrawable()
        shape.setColor(color)
        shape.cornerRadius = radius
        return shape
    }
}
