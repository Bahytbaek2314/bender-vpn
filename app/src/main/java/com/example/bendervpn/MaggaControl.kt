package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.io.ByteArrayInputStream
import java.util.concurrent.Executors

private val io = Executors.newSingleThreadExecutor()
private var backend: GoBackend? = null
private var job = 0

private class MaggaTunnel : Tunnel {
    override fun getName(): String = "magga"
    override fun onStateChange(newState: Tunnel.State) {}
}

private val tunnel = MaggaTunnel()

private fun AppCompatActivity.tunnelText(): String {
    return try {
        assets.open("almaty.conf").bufferedReader().use { it.readText() }.replace("\r\n", "\n")
    } catch (e: Exception) {
        ""
    }
}

fun AppCompatActivity.magga(
    up: Boolean,
    ask: ((Intent) -> Unit)? = null,
    done: ((Boolean) -> Unit)? = null
): Boolean {
    val saved = tunnelText()
    if (!saved.contains("[Interface]")) {
        Toast.makeText(this, "Конфиг сервера не вшит", Toast.LENGTH_LONG).show()
        return false
    }
    if (up) {
        val permit = VpnService.prepare(this)
        if (permit != null) {
            if (ask != null) ask(permit) else startActivity(permit)
            return false
        }
    }
    val mine = ++job
    val app = applicationContext
    io.execute {
        try {
            val box = backend ?: GoBackend(app).also { backend = it }
            if (up) {
                val config = Config.parse(ByteArrayInputStream(saved.toByteArray()))
                box.setState(tunnel, Tunnel.State.UP, config)
            } else {
                box.setState(tunnel, Tunnel.State.DOWN, null)
            }
            if (mine != job) return@execute
            runOnUiThread { done?.invoke(true) }
        } catch (e: Exception) {
            if (mine != job) return@execute
            runOnUiThread {
                Toast.makeText(this, e.message ?: "Туннель не запустился", Toast.LENGTH_LONG).show()
                done?.invoke(false)
            }
        }
    }
    return true
}
