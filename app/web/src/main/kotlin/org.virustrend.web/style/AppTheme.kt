package org.virustrend.web.style

import com.ccfraser.muirwik.components.styles.PaletteOptions
import com.ccfraser.muirwik.components.styles.Theme
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinext.js.jsObject

val appTheme = theme {
    palette = palette {
        type = "dark"
    }
}

private fun theme(block: ThemeOptions.() -> Unit): Theme =
    createMuiTheme(jsObject(block).unsafeCast<ThemeOptions>())

private fun palette(block: PaletteOptions.() -> Unit): PaletteOptions =
    jsObject(block).unsafeCast<PaletteOptions>()