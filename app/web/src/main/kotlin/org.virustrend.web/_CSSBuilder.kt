package org.virustrend.web

import kotlinx.css.CSSBuilder
import kotlinx.css.Visibility
import kotlinx.css.visibility

fun CSSBuilder.visibleIf(block: () -> Boolean) {
    visibility = if (block()) Visibility.visible else Visibility.hidden
}

fun CSSBuilder.visibleUnless(block: () -> Boolean) {
    visibility = if (block()) Visibility.hidden else Visibility.visible
}