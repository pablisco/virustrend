package org.virustrend.color

actual typealias PlatformColor = String

actual fun Color.toPlatformColor(): PlatformColor =
    "#${red.hex}${green.hex}${blue.hex}${(alpha * 255).hex}"

actual operator fun Color.Companion.invoke(platformColor: PlatformColor): Color =
    platformColor.withComponents { (red, green, blue, alpha) ->
        Color(alpha = alpha / 255.0, red = red, green = green, blue = blue)
    }

fun <R> PlatformColor.withComponents(block: (List<Int>) -> R): R =
    replace("#", "").chunked(2).map { it.fromHex() }.let(block)

private val Int.hex: String get() = toString(16)
private fun String.fromHex(): Int = toInt(16)
private val Double.hex: String get() = toInt().hex
