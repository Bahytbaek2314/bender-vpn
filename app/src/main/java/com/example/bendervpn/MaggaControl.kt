package com.example.bendervpn

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.magga(up: Boolean) {
    val wg = "com.wireguard.android"
    if (packageManager.getLaunchIntentForPackage(wg) == null) {
        Toast.makeText(this, "Установи приложение WireGuard", Toast.LENGTH_LONG).show()
        return
    }
    val action = if (up)
        "com.wireguard.android.action.SET_TUNNEL_UP"
    else
        "com.wireguard.android.action.SET_TUNNEL_DOWN"
    startActivity(Intent(action).apply {
        setPackage(wg)
        putExtra("tunnel", "magga")
    })
}
