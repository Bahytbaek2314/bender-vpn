package com.example.bendervpn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<Button>(R.id.registerBtn).setOnClickListener {
            Toast.makeText(this, "Регистрация", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.serversBtn).setOnClickListener {
            startActivity(Intent(this, ServerListActivity::class.java))
        }
        findViewById<Button>(R.id.settingsBtn).setOnClickListener {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
