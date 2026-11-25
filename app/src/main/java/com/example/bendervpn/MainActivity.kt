package com.example.bendervpn

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var vpnButton: PowerButton
    private lateinit var statusText: TextView
    private lateinit var serverText: TextView
    private lateinit var cityText: TextView
    private lateinit var pingText: TextView
    private lateinit var signalBars: SignalBars
    private lateinit var homeFlag: FlagView
    private lateinit var locationsBox: LinearLayout
    private lateinit var proBadge: TextView
    private lateinit var proState: TextView
    private lateinit var proKeyInput: EditText
    private lateinit var saveProKey: TextView
    private lateinit var clearProKey: TextView
    private lateinit var pages: List<View>
    private lateinit var icons: List<LineIcon>
    private lateinit var labels: List<TextView>
    private lateinit var bubbles: List<View>
    private lateinit var serverRow: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private var connectRunnable: Runnable? = null
    private var state = 0
    private val prefs by lazy { getSharedPreferences("vpn", MODE_PRIVATE) }

    private data class Place(
        val key: String,
        val title: String,
        val city: String,
        val ping: String,
        val pro: Boolean
    )

    private val places = listOf(
        Place("GERMANY", "Германия", "Франкфурт", "23 ms", false),
        Place("FRANCE", "Франция", "Париж", "29 ms", false),
        Place("RUSSIA", "Россия", "Москва", "19 ms", false),
        Place("ENGLAND", "Англия", "Лондон", "41 ms", true),
        Place("NETHERLANDS", "Нидерланды", "Амстердам", "26 ms", true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vpnButton = findViewById(R.id.vpnButton)
        statusText = findViewById(R.id.statusText)
        serverText = findViewById(R.id.serverText)
        cityText = findViewById(R.id.cityText)
        pingText = findViewById(R.id.pingText)
        signalBars = findViewById(R.id.signalBars)
        homeFlag = findViewById(R.id.homeFlag)
        locationsBox = findViewById(R.id.locationsBox)
        proBadge = findViewById(R.id.proBadge)
        proState = findViewById(R.id.proState)
        proKeyInput = findViewById(R.id.proKeyInput)
        saveProKey = findViewById(R.id.saveProKey)
        clearProKey = findViewById(R.id.clearProKey)
        pages = listOf(
            findViewById(R.id.homePage),
            findViewById(R.id.proPage),
            findViewById(R.id.locationsPage),
            findViewById(R.id.settingsPage)
        )
        icons = listOf(
            findViewById(R.id.iconHome),
            findViewById(R.id.iconPro),
            findViewById(R.id.iconLocations),
            findViewById(R.id.iconSettings)
        )
        labels = listOf(
            findViewById(R.id.labelHome),
            findViewById(R.id.labelPro),
            findViewById(R.id.labelLocations),
            findViewById(R.id.labelSettings)
        )
        bubbles = listOf(
            findViewById(R.id.bubbleHome),
            findViewById(R.id.bubblePro),
            findViewById(R.id.bubbleLocations),
            findViewById(R.id.bubbleSettings)
        )

        val density = resources.displayMetrics.density
        serverRow = findViewById(R.id.serverRow)
        serverRow.background = round(0xFF161616.toInt(), 22f * density, 0xFF3A3050.toInt())
        serverRow.setOnClickListener { showPage(2) }
        findViewById<LinearLayout>(R.id.bottomBar).background = round(0xFF141414.toInt(), 28f * density)

        findViewById<LinearLayout>(R.id.proCard).background = round(0xFF161616.toInt(), 18f * density)
        proKeyInput.background = round(0xFF161616.toInt(), 16f * density, 0xFF3A3050.toInt())
        saveProKey.background = round(0xFFB388FF.toInt(), 16f * density)
        proBadge.background = round(0xFF2A2040.toInt(), 100f * density)
        saveProKey.setOnClickListener { saveKey() }
        clearProKey.setOnClickListener { clearKey() }
        listOf(R.id.rowStatus, R.id.rowImport, R.id.rowSupport, R.id.rowTelegram).forEach { id ->
            findViewById<LinearLayout>(id).background = round(0xFF161616.toInt(), 16f * density)
        }
        findViewById<View>(R.id.rowStatus).setOnClickListener {
            startActivity(Intent(this, StatusActivity::class.java))
        }
        findViewById<View>(R.id.rowImport).setOnClickListener {
            startActivity(Intent(this, ImportActivity::class.java))
        }
        findViewById<View>(R.id.rowSupport).setOnClickListener {
            openInfo("Поддержка", "Сюда позже встанет почта и чат.")
        }
        findViewById<View>(R.id.rowTelegram).setOnClickListener {
            openInfo("Наш Telegram", "Ссылку на канал поставим, когда она будет.")
        }
        listOf(R.id.tabHome, R.id.tabPro, R.id.tabLocations, R.id.tabSettings)
            .forEachIndexed { index, id ->
                findViewById<View>(id).setOnClickListener { showPage(index) }
            }

        if (!prefs.contains("server")) {
            prefs.edit().putString("server", "GERMANY").putString("ping", "23 ms").apply()
        }
        showServer()
        refreshPro()
        paint()
        vpnButton.setOnClickListener { onPower() }
        showPage(0)
    }

    override fun onResume() {
        super.onResume()
        showServer()
        refreshPro()
    }

    private fun round(color: Int, radius: Float, stroke: Int = 0): GradientDrawable {
        val shape = GradientDrawable()
        shape.setColor(color)
        shape.cornerRadius = radius
        if (stroke != 0) {
            val width = (resources.displayMetrics.density * 1.2f).toInt().coerceAtLeast(1)
            shape.setStroke(width, stroke)
        }
        return shape
    }

    private fun hasPro() = !prefs.getString("wg_key", "").isNullOrBlank()

    private fun refreshPro() {
        val on = hasPro()
        proBadge.visibility = if (on) View.VISIBLE else View.GONE
        proState.text = if (on) {
            "Pro активен. Англия и Нидерланды открыты."
        } else {
            "Оплати в боте и вставь ключ сюда."
        }
        clearProKey.visibility = if (on) View.VISIBLE else View.GONE
        val saved = prefs.getString("wg_key", "") ?: ""
        if (proKeyInput.text.toString() != saved) proKeyInput.setText(saved)
    }

    private fun saveKey() {
        val key = proKeyInput.text.toString().trim()
        if (key.isEmpty()) {
            Toast.makeText(this, "Вставь ключ", Toast.LENGTH_SHORT).show()
            return
        }
        prefs.edit().putString("wg_key", key).apply()
        Toast.makeText(this, "Pro включён", Toast.LENGTH_SHORT).show()
        refreshPro()
    }

    private fun clearKey() {
        val current = placeOf(prefs.getString("server", "GERMANY"))
        val edit = prefs.edit().remove("wg_key")
        if (current.pro) {
            edit.putString("server", "GERMANY").putString("ping", "23 ms")
            connectRunnable?.let { handler.removeCallbacks(it) }
            state = 0
            paint()
        }
        edit.apply()
        proKeyInput.setText("")
        showServer()
        refreshPro()
        Toast.makeText(this, "Ключ удалён", Toast.LENGTH_SHORT).show()
    }

    private fun showPage(index: Int) {
        pages.forEachIndexed { i, view ->
            view.visibility = if (i == index) View.VISIBLE else View.GONE
        }
        val purple = 0xFFB388FF.toInt()
        val gray = 0xFF8E8E8E.toInt()
        icons.forEachIndexed { i, icon -> icon.accent = i == index }
        labels.forEachIndexed { i, label ->
            label.setTextColor(if (i == index) purple else gray)
        }
        bubbles.forEachIndexed { i, bubble ->
            bubble.background = if (i == index) round(0xFF2A2040.toInt(), 100f) else null
        }
        if (index == 2) fillLocations()
    }

    private fun fillLocations() {
        locationsBox.removeAllViews()
        val density = resources.displayMetrics.density
        fun dp(v: Int) = (v * density).toInt()
        val selected = prefs.getString("server", "GERMANY")
        places.forEach { place ->
            val chosen = place.key == selected
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER_VERTICAL
            row.setPadding(dp(14), dp(14), dp(14), dp(14))
            row.background = round(if (chosen) 0xFF1C1428.toInt() else 0xFF161616.toInt(), 16f * density)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(10)
            row.layoutParams = params

            val flag = FlagView(this)
            flag.code = codeOf(place.key)
            flag.chosen = chosen
            val flagParams = LinearLayout.LayoutParams(dp(40), dp(28))
            flagParams.marginEnd = dp(12)
            flag.layoutParams = flagParams

            val texts = LinearLayout(this)
            texts.orientation = LinearLayout.VERTICAL
            texts.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            val title = TextView(this)
            title.text = place.title
            title.textSize = 16f
            title.setTextColor(if (chosen) 0xFFB388FF.toInt() else 0xFFFFFFFF.toInt())
            val city = TextView(this)
            city.text = place.city
            city.textSize = 13f
            city.setTextColor(0xFF8E8E8E.toInt())
            texts.addView(title)
            texts.addView(city)
            row.addView(flag)
            row.addView(texts)

            val unlocked = !place.pro || hasPro()
            if (place.pro) {
                val badge = TextView(this)
                badge.text = "Pro"
                badge.textSize = 12f
                badge.setTextColor(0xFFB388FF.toInt())
                badge.setPadding(dp(10), dp(4), dp(10), dp(4))
                badge.background = round(0xFF2A2040.toInt(), 20f * density)
                val badgeParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                badgeParams.marginEnd = dp(10)
                badge.layoutParams = badgeParams
                row.addView(badge)
            }
            if (unlocked) {
                val pingView = TextView(this)
                pingView.text = place.ping
                pingView.textSize = 14f
                pingView.setTextColor(0xFF3DDC84.toInt())
                row.addView(pingView)
            }

            row.setOnClickListener {
                if (!unlocked) {
                    showPage(1)
                } else {
                    prefs.edit().putString("server", place.key).putString("ping", place.ping).apply()
                    connectRunnable?.let { handler.removeCallbacks(it) }
                    state = 0
                    showServer()
                    paint()
                    showPage(0)
                }
            }
            locationsBox.addView(row)
        }
    }

    private fun codeOf(name: String) = when (name) {
        "FRANCE" -> "FR"
        "ENGLAND" -> "GB"
        "NETHERLANDS" -> "NL"
        "RUSSIA" -> "RU"
        else -> "DE"
    }

    private fun placeOf(name: String?) = places.firstOrNull { it.key == name } ?: places[0]

    private fun showServer() {
        val place = placeOf(prefs.getString("server", "GERMANY"))
        val ping = prefs.getString("ping", place.ping) ?: place.ping
        serverText.text = place.title
        cityText.text = place.city
        pingText.text = ping
        val digits = ping.filter { it.isDigit() }
        val ms = if (digits.isEmpty()) 99 else digits.toInt()
        signalBars.pingMs = ms
        pingText.setTextColor(if (ms <= 80) 0xFF3DDC84.toInt() else 0xFFFF4D4D.toInt())
        homeFlag.code = codeOf(place.key)
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
        val density = resources.displayMetrics.density
        if (state == 2) {
            serverRow.background = round(0xFF1C1428.toInt(), 22f * density, 0xFFB388FF.toInt())
        } else {
            serverRow.background = round(0xFF161616.toInt(), 22f * density, 0xFF3A3050.toInt())
        }
        prefs.edit().putString("link_state", label).apply()
    }

    private fun openInfo(title: String, body: String) {
        startActivity(
            Intent(this, InfoActivity::class.java)
                .putExtra("title", title)
                .putExtra("body", body)
        )
    }

    override fun onDestroy() {
        connectRunnable?.let { handler.removeCallbacks(it) }
        super.onDestroy()
    }
}
