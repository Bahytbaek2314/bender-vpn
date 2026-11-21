package com.example.bendervpn

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<TextView>(R.id.backButton).setOnClickListener { finish() }

        val prefs = getSharedPreferences("vpn", MODE_PRIVATE)
        val languages = arrayOf("Русский", "English", "Қазақша")
        val spinner = findViewById<Spinner>(R.id.languageSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val saved = prefs.getString("language", "Русский")
        val index = languages.indexOf(saved).coerceAtLeast(0)
        spinner.setSelection(index)

        var ready = false
        spinner.post { ready = true }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                prefs.edit().putString("language", languages[position]).apply()
                if (ready) {
                    Toast.makeText(this@SettingsActivity, "Язык: ${languages[position]}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
