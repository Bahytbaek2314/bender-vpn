package com.example.bendervpn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val register = intent.getStringExtra("mode") == "register"
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        loginBtn.text = if (register) "Регистрация" else "Войти"

        loginBtn.setOnClickListener {
            val mail = email.text.toString().trim()
            val pass = password.text.toString()
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            getSharedPreferences("vpn", MODE_PRIVATE).edit()
                .putBoolean("logged_in", true)
                .putString("email", mail)
                .apply()
            Toast.makeText(
                this,
                if (register) "Аккаунт создан" else "Вход выполнен",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}
