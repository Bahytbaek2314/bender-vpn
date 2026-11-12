package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.vpnButton).setOnClickListener {
            val intent = VpnService.prepare(this)
            if (intent != null) startActivityForResult(intent, 1)
            else startService(Intent(this, SimpleVpnService::class.java))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            startService(Intent(this, SimpleVpnService::class.java))
            Toast.makeText(this, "VPN запущен", Toast.LENGTH_SHORT).show()
        }
    }
}
