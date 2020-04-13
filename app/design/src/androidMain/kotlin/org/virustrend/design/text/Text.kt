package org.virustrend.design.text

actual sealed class Text {

    data class Resource(val value: Int) : Text()
    data class String(val value: CharSequence) : Text()

}
