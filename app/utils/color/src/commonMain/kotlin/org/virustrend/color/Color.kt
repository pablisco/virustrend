package org.virustrend.color

import kotlin.math.pow
import kotlin.math.roundToInt

data class Color(val alpha: Double, val red: Int, val green: Int, val blue: Int) {
    companion object {
        val white = Color(alpha = 1.0, red = 255, green = 255, blue = 255)
        val red = Color(alpha = 1.0, red = 255, green = 0, blue = 0)

    }
}

data class ColorRange(val start: Color, val end: Color) {

    companion object {
        val whiteToRed = ColorRange(Color.white, Color.red)
    }

    val alpha = start.alpha to end.alpha
    val red = start.red to end.red
    val green = start.green to end.green
    val blue = start.blue to end.blue

}

fun ColorRange.colorAt(percentage: Float): Color = colorAt(percentage.toDouble())
fun ColorRange.colorAt(p: Double): Color =
    Color(alpha = alpha / p, red = red / p, green = green / p, blue = blue / p)

private operator fun Pair<Double, Double>.div(percentage: Double): Double =
    percentage.interpolate(
        from = first.toRelative(),
        to = second.toRelative()
    ).toAbsolute().toDouble()

private operator fun Pair<Int, Int>.div(percentage: Double): Int =
    percentage.interpolate(
        from = first.toRelative().toLinear(),
        to = second.toRelative().toLinear()
    ).toRGB().toAbsolute()

private fun Double.interpolate(from: Double, to: Double): Double =
    from + this * (to - from)

private fun Double.toLinear(): Double = pow(2.2)
private fun Double.toRGB(): Double = pow(1.0 / 2.2)

private fun Int.toRelative(): Double = toDouble().toRelative()
private fun Double.toRelative(): Double = (this / 255.0f)
private fun Double.toAbsolute(): Int = (this * 255f).roundToInt()