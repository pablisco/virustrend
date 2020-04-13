package org.virustrend.graphs

import android.graphics.Path

actual typealias PlatformPath = Path

actual fun CountryPath.toPlatformPath(): PlatformPath = Path().apply {
    path.split("[ ,]")


}