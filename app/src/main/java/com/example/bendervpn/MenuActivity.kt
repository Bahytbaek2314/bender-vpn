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

        findViewById<Button>(R.id.premiumBtn).setOnClickListener {
            Toast.makeText(this, "Премиум в разработке", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.restoreBtn).setOnClickListener {
            Toast.makeText(this, "Восстановление подписки", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.pcBtn).setOnClickListener {
            Toast.makeText(this, "Установка на ПК/Mac", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.messagesBtn).setOnClickListener {
            Toast.makeText(this, "Сообщения", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.helpBtn).setOnClickListener {
            Toast.makeText(this, "Помощь и справка", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.loginBtn).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
