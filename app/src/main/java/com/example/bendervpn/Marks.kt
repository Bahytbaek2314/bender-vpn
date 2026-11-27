package com.example.bendervpn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class FlagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var code: String = "DE"
    var chosen: Boolean = false
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clip = Path()

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val radius = h * 0.18f
        clip.reset()
        clip.addRoundRect(1f, 1f, w - 1f, h - 1f, radius, radius, Path.Direction.CW)
        canvas.save()
        canvas.clipPath(clip)
        paint.style = Paint.Style.FILL
        when (code) {
            "FR" -> bandsV(canvas, w, h, 0xFF0055A4.toInt(), 0xFFFFFFFF.toInt(), 0xFFEF4135.toInt())
            "NL" -> bandsH(canvas, w, h, 0xFFAE1C28.toInt(), 0xFFFFFFFF.toInt(), 0xFF21468B.toInt())
            "RU" -> bandsH(canvas, w, h, 0xFFFFFFFF.toInt(), 0xFF0039A6.toInt(), 0xFFD52B1E.toInt())
            "KZ" -> kazakhstan(canvas, w, h)
            "GB" -> england(canvas, w, h)
            else -> bandsH(canvas, w, h, 0xFF000000.toInt(), 0xFFDD0000.toInt(), 0xFFFFCE00.toInt())
        }
        canvas.restore()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = if (chosen) h * 0.07f else h * 0.035f
        paint.color = if (chosen) 0xFFB388FF.toInt() else 0x33FFFFFF
        canvas.drawRoundRect(
            paint.strokeWidth / 2f,
            paint.strokeWidth / 2f,
            w - paint.strokeWidth / 2f,
            h - paint.strokeWidth / 2f,
            radius,
            radius,
            paint
        )
    }

    private fun bandsH(canvas: Canvas, w: Float, h: Float, a: Int, b: Int, c: Int) {
        paint.color = a
        canvas.drawRect(0f, 0f, w, h / 3f, paint)
        paint.color = b
        canvas.drawRect(0f, h / 3f, w, h * 2f / 3f, paint)
        paint.color = c
        canvas.drawRect(0f, h * 2f / 3f, w, h, paint)
    }

    private fun bandsV(canvas: Canvas, w: Float, h: Float, a: Int, b: Int, c: Int) {
        paint.color = a
        canvas.drawRect(0f, 0f, w / 3f, h, paint)
        paint.color = b
        canvas.drawRect(w / 3f, 0f, w * 2f / 3f, h, paint)
        paint.color = c
        canvas.drawRect(w * 2f / 3f, 0f, w, h, paint)
    }

    private fun england(canvas: Canvas, w: Float, h: Float) {
        paint.color = 0xFFFFFFFF.toInt()
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.color = 0xFFCF142B.toInt()
        val band = h * 0.22f
        canvas.drawRect(0f, (h - band) / 2f, w, (h + band) / 2f, paint)
        val upright = w * 0.16f
        canvas.drawRect((w - upright) / 2f, 0f, (w + upright) / 2f, h, paint)
    }
}

private fun kazakhstan(canvas: Canvas, w: Float, h: Float) {
        paint.color = 0xFF00AFC8.toInt()
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.color = 0xFFFEC50C.toInt()
        canvas.drawCircle(w * 0.5f, h * 0.42f, h * 0.22f, paint)
        canvas.drawRect(w * 0.15f, h * 0.76f, w * 0.85f, h * 0.84f, paint)
    }
}

class CrownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFC107.toInt()
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val path = Path()
        path.moveTo(w * 0.08f, h * 0.62f)
        path.lineTo(w * 0.08f, h * 0.34f)
        path.lineTo(w * 0.30f, h * 0.52f)
        path.lineTo(w * 0.50f, h * 0.16f)
        path.lineTo(w * 0.70f, h * 0.52f)
        path.lineTo(w * 0.92f, h * 0.34f)
        path.lineTo(w * 0.92f, h * 0.62f)
        path.close()
        canvas.drawPath(path, paint)
        canvas.drawRoundRect(w * 0.08f, h * 0.68f, w * 0.92f, h * 0.84f, h * 0.08f, h * 0.08f, paint)
    }
}
