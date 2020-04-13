@file:JvmName("AndroidPlatformColor")
package org.virustrend.color

import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import android.graphics.Color as AndroidColor

actual typealias PlatformColor = Int

actual fun Color.toPlatformColor() : PlatformColor =
    AndroidColor.argb((alpha * 255).toInt(), red, green, blue)

actual operator fun Color.Companion.invoke(platformColor: PlatformColor): Color = with(platformColor) {
    Color(alpha = alpha / 255.0, red = red, green = green, blue = blue)
}
