package org.virustrend.color

expect class PlatformColor
expect fun Color.toPlatformColor() : PlatformColor
expect operator fun Color.Companion.invoke(platformColor: PlatformColor) : Color

fun PlatformColor.asColor(): Color = Color(this)
