package org.virustrend.design.text

actual sealed class Text {

    data class String(val value: CharSequence) : Text()

}