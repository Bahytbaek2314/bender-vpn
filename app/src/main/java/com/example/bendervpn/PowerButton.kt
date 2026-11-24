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
            val ir = r * 0.42f
            canvas.drawArc(cx - ir, cy - ir, cx + ir, cy + ir, 140f, 260f, false, paint)
            canvas.drawLine(cx, cy - r * 0.52f, cx, cy - r * 0.02f, paint)
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(spin)
        super.onDetachedFromWindow()
    }
}
