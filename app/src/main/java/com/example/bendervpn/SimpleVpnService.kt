package com.example.bendervpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class SimpleVpnService : VpnService() {
    private var iface: ParcelFileDescriptor? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        iface = Builder()
            .setSession("BenderVPN")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .establish()
        return START_STICKY
    }
    override fun onDestroy() {
        iface?.close()
        super.onDestroy()
    }
}
