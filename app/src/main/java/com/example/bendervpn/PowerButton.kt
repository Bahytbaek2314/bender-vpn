package com.example.bendervpn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class PowerButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var mode = 0
    private var angle = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val path = Path()
    private val spin = object : Runnable {
        override fun run() {
            if (mode != 1) return
            angle = (angle + 7f) % 360f
            invalidate()
            postOnAnimation(this)
        }
    }

    fun setMode(next: Int) {
        mode = next
        removeCallbacks(spin)
        if (mode == 1) postOnAnimation(spin)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val cy = height / 2f
        val r = min(width, height) * 0.36f
        val stroke = r * 0.075f
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = stroke
        paint.color = 0xFFB388FF.toInt()
        if (mode == 1) {
            paint.alpha = 50
            canvas.drawCircle(cx, cy, r, paint)
            paint.alpha = 255
            canvas.drawArc(cx - r, cy - r, cx + r, cy + r, angle, 100f, false, paint)
        } else {
            paint.alpha = 255
            canvas.drawCircle(cx, cy, r, paint)
        }
        paint.color = 0xFFFFFFFF.toInt()
        paint.strokeWidth = stroke
        if (mode == 2) {
            path.reset()
            path.moveTo(cx - r * 0.36f, cy + r * 0.02f)
            path.lineTo(cx - r * 0.08f, cy + r * 0.30f)
            path.lineTo(cx + r * 0.40f, cy - r * 0.28f)
            canvas.drawPath(path, paint)
        } else if (mode == 0) {
            val headR = r * 0.46f
            val headY = cy + r * 0.08f
            val earR = r * 0.20f
            val earX = headR * 0.70f
            val earY = headY - headR * 0.72f
            paint.style = Paint.Style.FILL
            paint.color = 0xFFFFFFFF.toInt()
            canvas.drawCircle(cx - earX, earY, earR, paint)
            canvas.drawCircle(cx + earX, earY, earR, paint)
            canvas.drawCircle(cx, headY, headR, paint)
            paint.color = 0xFFC9A6FF.toInt()
            val innerR = earR * 0.46f
            val innerY = earY + earR * 0.08f
            canvas.drawCircle(cx - earX, innerY, innerR, paint)
            canvas.drawCircle(cx + earX, innerY, innerR, paint)
            paint.color = 0xFFF3ECFF.toInt()
            val mw = headR * 0.46f
            val mh = headR * 0.32f
            val my = headY + headR * 0.34f
            canvas.drawOval(cx - mw, my - mh * 0.75f, cx + mw, my + mh * 0.85f, paint)
            paint.color = 0xFF1C1024.toInt()
            val eyeY = headY - headR * 0.06f
            val eyeR = r * 0.045f
            canvas.drawCircle(cx - headR * 0.36f, eyeY, eyeR, paint)
            canvas.drawCircle(cx + headR * 0.36f, eyeY, eyeR, paint)
            canvas.drawOval(cx - r * 0.085f, my - r * 0.09f, cx + r * 0.085f, my + r * 0.02f, paint)
            paint.color = 0xFFFFFFFF.toInt()
            canvas.drawCircle(cx - headR * 0.36f - eyeR * 0.28f, eyeY - eyeR * 0.30f, eyeR * 0.22f, paint)
            canvas.drawCircle(cx + headR * 0.36f - eyeR * 0.28f, eyeY - eyeR * 0.30f, eyeR * 0.22f, paint)
            val lensW = r * 0.155f
            val lensH = r * 0.118f
            val gap = r * 0.055f
            val leftX = cx - gap / 2f - lensW
            val rightX = cx + gap / 2f + lensW
            val frame = stroke * 0.22f
            val rad = lensH * 0.42f
            val bridge = lensH * 0.42f
            paint.style = Paint.Style.FILL
            paint.color = 0xFF1C1024.toInt()
            canvas.drawRect(leftX + lensW, eyeY - bridge, rightX - lensW, eyeY + bridge, paint)
            canvas.drawRoundRect(leftX - lensW, eyeY - lensH, leftX + lensW, eyeY + lensH, rad, rad, paint)
            canvas.drawRoundRect(rightX - lensW, eyeY - lensH, rightX + lensW, eyeY + lensH, rad, rad, paint)
            paint.style = Paint.Style.STROKE
            paint.color = 0xFFB388FF.toInt()
            paint.strokeWidth = frame
            canvas.drawRoundRect(leftX - lensW, eyeY - lensH, leftX + lensW, eyeY + lensH, rad, rad, paint)
            canvas.drawRoundRect(rightX - lensW, eyeY - lensH, rightX + lensW, eyeY + lensH, rad, rad, paint)
            canvas.drawLine(leftX + lensW, eyeY - bridge, rightX - lensW, eyeY - bridge, paint)
            canvas.drawLine(leftX + lensW, eyeY + bridge, rightX - lensW, eyeY + bridge, paint)
            canvas.drawLine(leftX - lensW, eyeY - lensH * 0.15f, leftX - lensW - r * 0.055f, eyeY - lensH * 0.70f, paint)
            canvas.drawLine(rightX + lensW, eyeY - lensH * 0.15f, rightX + lensW + r * 0.055f, eyeY - lensH * 0.70f, paint)
            paint.color = 0xFFFFFFFF.toInt()
            paint.strokeWidth = frame * 0.65f
            canvas.drawLine(leftX - lensW * 0.42f, eyeY - lensH * 0.22f, leftX - lensW * 0.12f, eyeY - lensH * 0.55f, paint)
            canvas.drawLine(rightX - lensW * 0.42f, eyeY - lensH * 0.22f, rightX - lensW * 0.12f, eyeY - lensH * 0.55f, paint)
            paint.strokeWidth = stroke
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(spin)
        super.onDetachedFromWindow()
    }
}
