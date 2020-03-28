package org.virustrend.android.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.pixplicity.sharp.OnSvgElementListener
import com.pixplicity.sharp.Sharp

fun Sharp.whenSvgElementReady(block: (id: String?, paint: Paint?) -> Unit): Sharp = apply {
    setOnElementListener(object : OnSvgElementListener {
        override fun <T : Any?> onSvgElement(id: String?, it: T, eb: RectF?, c: Canvas, cb: RectF?, paint: Paint?): T =
            it.apply { block(id, paint) }
        override fun onSvgEnd(canvas: Canvas, bounds: RectF?) = Unit
        override fun onSvgStart(canvas: Canvas, bounds: RectF?) = Unit
        override fun <T : Any?> onSvgElementDrawn(id: String?, element: T, canvas: Canvas, paint: Paint?) = Unit
    })
}