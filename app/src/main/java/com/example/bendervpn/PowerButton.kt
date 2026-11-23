package com.example.bendervpn

import android.content.Context
import android.graphics.ImageDecoder
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PowerButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    fun showStill(resId: Int) {
        stopAnim()
        setImageResource(resId)
    }

    fun playRaw(rawId: Int, loop: Boolean, end: (() -> Unit)? = null) {
        stopAnim()
        try {
            val drawable = ImageDecoder.decodeDrawable(ImageDecoder.createSource(resources, rawId))
            setImageDrawable(drawable)
            if (drawable is AnimatedImageDrawable) {
                drawable.repeatCount = if (loop) AnimatedImageDrawable.REPEAT_INFINITE else 0
                if (!loop && end != null) {
                    drawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                        override fun onAnimationEnd(d: Drawable?) {
                            post { end.invoke() }
                        }
                    })
                }
                drawable.start()
                return
            }
        } catch (e: Exception) {
        }
        end?.invoke()
    }

    private fun stopAnim() {
        val current = drawable
        if (current is AnimatedImageDrawable) current.stop()
    }

    override fun onDetachedFromWindow() {
        stopAnim()
        super.onDetachedFromWindow()
    }
}