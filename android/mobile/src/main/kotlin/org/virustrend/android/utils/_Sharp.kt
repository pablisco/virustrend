package org.virustrend.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.pixplicity.sharp.OnSvgElementListener
import com.pixplicity.sharp.Sharp

fun Sharp.onSvgElementDrawn(block: (id: String?, paint: Paint?) -> Unit) {
    setOnElementListener(object : OnSvgElementListener {

        override fun <T : Any?> onSvgElement(
            id: String?,
            element: T,
            elementBounds: RectF?,
            canvas: Canvas,
            canvasBounds: RectF?,
            paint: Paint?
        ): T = element.apply {
            block(id, paint)
        }

        override fun onSvgEnd(canvas: Canvas, bounds: RectF?) = Unit
        override fun onSvgStart(canvas: Canvas, bounds: RectF?) = Unit
        override fun <T : Any?> onSvgElementDrawn(id: String?, element: T, canvas: Canvas, paint: Paint?) =
            block(id, paint)
    })
}