package org.virustrend.web.views

import com.ccfraser.muirwik.components.card.mCard
import kotlinx.css.*
import org.virustrend.web.utils.toLocaleString
import react.RBuilder
import styled.css
import styled.styledDiv

fun RBuilder.counterBox(name: String, count: Int) = styledDiv {
    css {
        flexGrow = 1.0
        margin(.5.vw)
        media("only screen and (max-width: 600px)") {
            width = 45.vw
        }
    }
    mCard {
        css {
            padding(16.px)
            backgroundColor = Color.white
        }
        styledDiv {
            css {
                color = Color.black
                fontSize = 16.px
                textAlign = TextAlign.center
            }
            +name
        }
        styledDiv {
            css {
                color = Color.black
                fontSize = 32.px
                textAlign = TextAlign.center
            }
            +"${count.toLocaleString()}"
        }
    }
}