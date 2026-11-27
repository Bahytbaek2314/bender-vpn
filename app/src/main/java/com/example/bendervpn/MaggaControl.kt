package com.example.bendervpn

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

private const val WG = "com.wireguard.android"
private const val WG_PERM = "com.wireguard.android.permission.CONTROL_TUNNELS"

fun AppCompatActivity.magga(up: Boolean): Boolean {
    if (packageManager.getLaunchIntentForPackage(WG) == null) {
        Toast.makeText(this, "Установи приложение WireGuard", Toast.LENGTH_LONG).show()
        return false
    }
    val prefs = getSharedPreferences("vpn", android.content.Context.MODE_PRIVATE)
    val saved = prefs.getString("wg_key", "")?.trim().orEmpty()
    val server = prefs.getString("server", "") ?: ""
    val named = saved.isNotEmpty() &&
        !saved.contains("[") &&
        !saved.contains(" ") &&
        saved.length <= 15
    val tunnel = when {
        named -> saved
        server == "ALMATY" || saved.contains("[Interface]") -> "almaty"
        else -> "magga"
    }
    if (up && saved.contains("[Interface]")) {
        val file = File(cacheDir, "almaty.conf")
        file.writeText(saved)
        val hash = saved.hashCode()
        if (prefs.getInt("wg_hash", 0) != hash) {
            prefs.edit().putInt("wg_hash", hash).apply()
            val uri = FileProvider.getUriForFile(this, "com.example.bendervpn.fileprovider", file)
            val open = Intent(Intent.ACTION_VIEW)
            open.setDataAndType(uri, "application/x-wireguard-profile")
            open.setPackage(WG)
            open.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            grantUriPermission(WG, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(open)
            Toast.makeText(
                this,
                "В WireGuard нажми сохранить. Потом ещё раз кнопку в MAGGA",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }
    if (checkSelfPermission(WG_PERM) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(WG_PERM), 77)
        Toast.makeText(this, "Разреши управление, потом нажми кнопку ещё раз", Toast.LENGTH_LONG).show()
        return false
    }
    val action = if (up)
        "com.wireguard.android.action.SET_TUNNEL_UP"
    else
        "com.wireguard.android.action.SET_TUNNEL_DOWN"
    val intent = Intent(action)
    intent.component = ComponentName(WG, "com.wireguard.android.model.TunnelManager\$IntentReceiver")
    intent.putExtra("tunnel", tunnel)
    sendBroadcast(intent)
    return true
}
