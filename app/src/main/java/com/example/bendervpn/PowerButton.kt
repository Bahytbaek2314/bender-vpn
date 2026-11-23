package com.example.bendervpn

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PowerButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var movie: Movie? = null
    private var movieStart = 0L
    private var looping = false
    private var playing = false
    private var onEnd: (() -> Unit)? = null
    private val tick = Handler(Looper.getMainLooper())
    private val frame = object : Runnable {
        override fun run() {
            val m = movie
            if (!playing || m == null) return
            val dur = if (m.duration() <= 0) 1000 else m.duration()
            val rel = (SystemClock.uptimeMillis() - movieStart).toInt()
            if (!looping && rel >= dur) {
                m.setTime(dur - 1)
                invalidate()
                playing = false
                val cb = onEnd
                onEnd = null
                cb?.invoke()
                return
            }
            m.setTime(rel % dur)
            invalidate()
            tick.postDelayed(this, 16)
        }
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun showStill(resId: Int) {
        stopMovie()
        setImageResource(resId)
    }

    fun playRaw(rawId: Int, loop: Boolean, end: (() -> Unit)? = null) {
        stopMovie()
        setImageDrawable(null)
        val input = resources.openRawResource(rawId)
        movie = Movie.decodeStream(input)
        input.close()
        if (movie == null) {
            end?.invoke()
            return
        }
        looping = loop
        onEnd = end
        playing = true
        movieStart = SystemClock.uptimeMillis()
        tick.post(frame)
    }

    private fun stopMovie() {
        playing = false
        tick.removeCallbacks(frame)
        movie = null
        onEnd = null
    }

    override fun onDraw(canvas: Canvas) {
        val m = movie
        if (m == null) {
            super.onDraw(canvas)
            return
        }
        val mw = m.width().coerceAtLeast(1)
        val mh = m.height().coerceAtLeast(1)
        val s = minOf(width.toFloat() / mw, height.toFloat() / mh)
        canvas.save()
        canvas.translate((width - mw * s) / 2f, (height - mh * s) / 2f)
        canvas.scale(s, s)
        m.draw(canvas, 0f, 0f)
        canvas.restore()
    }

    override fun onDetachedFromWindow() {
        stopMovie()
        super.onDetachedFromWindow()
    }
}