package com.example.bendervpn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SignalBars @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var pingMs: Int = 23
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        val lit = when {
            pingMs <= 50 -> 4
            pingMs <= 80 -> 3
            pingMs <= 130 -> 2
            else -> 1
        }
        val on = if (pingMs <= 80) Color.parseColor("#3DDC84") else Color.parseColor("#FF4D4D")
        val off = Color.parseColor("#333333")
        val gap = width * 0.10f
        val barW = (width - gap * 3f) / 4f
        for (i in 0 until 4) {
            paint.color = if (i < lit) on else off
            val left = i * (barW + gap)
            val top = height * (0.78f - i * 0.22f)
            canvas.drawRoundRect(left, top, left + barW, height.toFloat(), 2f, 2f, paint)
        }
    }
}