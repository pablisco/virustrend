package org.virustrend.android.utils

import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.toColorBetween(start: Int, end: Int): Int =
    colorOf(
        alpha = componentBetween(start, end) { alpha.toRelative() }.toAbsolute(),
        red = componentBetween(start, end) { red.toRelative().toLinear() }.toRGB().toAbsolute(),
        green = componentBetween(start, end) { green.toRelative().toLinear() }.toRGB().toAbsolute(),
        blue = componentBetween(start, end) { blue.toRelative().toLinear() }.toRGB().toAbsolute()
    )

fun colorOf(alpha: Int, red: Int, green: Int, blue: Int): Int =
    (alpha shl 24) or (red shl 16) or (green shl 8) or blue

private fun Float.toLinear(): Float = pow(2.2f)
private fun Float.toRGB(): Float = pow(1.0f / 2.2f)

private fun Int.toRelative(): Float = (this.toFloat() / 255.0f)
private fun Float.toAbsolute(): Int = (this * 255f).roundToInt()

private inline fun Float.componentBetween(
    from: Int,
    to: Int,
    block: Int.() -> Float
): Float = interpolate(from.block(), to.block())

private fun Float.interpolate(from: Float, to: Float): Float =
    from + this * (to - from)