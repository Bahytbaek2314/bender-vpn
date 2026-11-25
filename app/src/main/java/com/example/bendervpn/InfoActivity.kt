package com.example.bendervpn

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        findViewById<TextView>(R.id.pageTitle).text = intent.getStringExtra("title")
        findViewById<TextView>(R.id.pageBody).text = intent.getStringExtra("body")
        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }
        val density = resources.displayMetrics.density
        val shape = GradientDrawable()
        shape.setColor(0xFF161616.toInt())
        shape.cornerRadius = 18f * density
        findViewById<LinearLayout>(R.id.infoCard).background = shape
    }
}
