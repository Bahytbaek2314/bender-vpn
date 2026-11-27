package com.example.bendervpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MaggaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.getSharedPreferences("vpn", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("want_disconnect", true)
            .apply()
    }
}
