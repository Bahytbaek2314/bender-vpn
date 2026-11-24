package com.example.bendervpn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class GlowBackground @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height * 0.46f
        paint.shader = RadialGradient(
            cx, cy, width * 0.95f,
            intArrayOf(0x66B388FF.toInt(), 0x22101828.toInt(), 0xFF050505.toInt()),
            floatArrayOf(0f, 0.42f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }
}

class LineIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        paint.strokeWidth = min(w, h) * 0.09f
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        when (tag?.toString()) {
            "bars" -> bars(canvas, w, h)
            "status" -> status(canvas, w, h)
            "key" -> key(canvas, w, h)
            "chat" -> chat(canvas, w, h)
            "plane" -> plane(canvas, w, h)
            else -> gear(canvas, w, h)
        }
    }

    private fun bars(c: Canvas, w: Float, h: Float) {
        val l = w * 0.18f
        val r = w * 0.82f
        c.drawLine(l, h * 0.28f, r, h * 0.28f, paint)
        c.drawLine(l, h * 0.50f, r, h * 0.50f, paint)
        c.drawLine(l, h * 0.72f, r, h * 0.72f, paint)
    }

    private fun status(c: Canvas, w: Float, h: Float) {
        c.drawCircle(w / 2f, h / 2f, w * 0.34f, paint)
        paint.style = Paint.Style.FILL
        c.drawCircle(w / 2f, h / 2f, w * 0.09f, paint)
        paint.style = Paint.Style.STROKE
    }

    private fun key(c: Canvas, w: Float, h: Float) {
        c.drawCircle(w * 0.32f, h * 0.36f, w * 0.16f, paint)
        c.drawLine(w * 0.46f, h * 0.48f, w * 0.84f, h * 0.80f, paint)
        c.drawLine(w * 0.62f, h * 0.62f, w * 0.74f, h * 0.50f, paint)
        c.drawLine(w * 0.74f, h * 0.74f, w * 0.86f, h * 0.62f, paint)
    }

    private fun chat(c: Canvas, w: Float, h: Float) {
        val r = w * 0.12f
        c.drawRoundRect(w * 0.12f, h * 0.14f, w * 0.88f, h * 0.68f, r, r, paint)
        c.drawLine(w * 0.30f, h * 0.68f, w * 0.22f, h * 0.86f, paint)
        c.drawLine(w * 0.22f, h * 0.86f, w * 0.46f, h * 0.68f, paint)
    }

    private fun plane(c: Canvas, w: Float, h: Float) {
        path.reset()
        path.moveTo(w * 0.14f, h * 0.50f)
        path.lineTo(w * 0.88f, h * 0.14f)
        path.lineTo(w * 0.56f, h * 0.86f)
        path.lineTo(w * 0.46f, h * 0.56f)
        path.close()
        c.drawPath(path, paint)
        c.drawLine(w * 0.48f, h * 0.54f, w * 0.78f, h * 0.28f, paint)
    }

    private fun gear(c: Canvas, w: Float, h: Float) {
        val cx = w / 2f
        val cy = h / 2f
        val r = w * 0.26f
        c.drawCircle(cx, cy, r, paint)
        for (i in 0 until 8) {
            val a = Math.toRadians((i * 45).toDouble())
            val x1 = cx + cos(a).toFloat() * r * 1.2f
            val y1 = cy + sin(a).toFloat() * r * 1.2f
            val x2 = cx + cos(a).toFloat() * w * 0.46f
            val y2 = cy + sin(a).toFloat() * w * 0.46f
            c.drawLine(x1, y1, x2, y2, paint)
        }
    }
}