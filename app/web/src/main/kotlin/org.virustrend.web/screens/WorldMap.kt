package org.virustrend.web.screens

import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onLoadFunction
import org.virustrend.Country
import org.virustrend.color.ColorRange
import org.virustrend.color.colorAt
import org.virustrend.color.toPlatformColor
import org.virustrend.domain.Screen
import org.virustrend.web.views.counterBox
import org.w3c.dom.Document
import org.w3c.dom.HTMLObjectElement
import org.w3c.dom.events.Event
import react.RBuilder
import styled.css
import styled.styledFooter
import styled.styledObject_
import kotlin.browser.document


fun RBuilder.renderWorldMap(screen: Screen.WorldMap) = with(screen) {
    mapGraph(cases = countryToMetric)
    styledFooter {
        css {
            flex(flexGrow = 0.0)
            padding(.5.vw)
            display = Display.flex
            justifyContent = JustifyContent.spaceEvenly
            flexDirection = FlexDirection.row
            flexWrap = FlexWrap.wrap
        }
        val (confirmed, deaths, recovered, active) = cases
        counterBox("Confirmed", confirmed)
        counterBox("Deaths", deaths)
        counterBox("Recovered", recovered)
        counterBox("Active", active)
    }
}

private fun RBuilder.mapGraph(cases: List<Pair<Country, Int>>) =
    styledObject_ {
        css {
            flex(flexGrow = 1.0)
            height = 0.px // make sure it follows flex-box
        }
        attrs {
            classes += "map"
            data = "world.svg"
            type = "image/svg+xml"
            onLoadFunction = renderMapGraph(cases)
        }
    }


private fun renderMapGraph(cases: List<Pair<Country, Int>>): (Event) -> Unit = {
    cases.sortedBy { (_, counter) -> counter }.forEachIndexed { index, (country, _) ->
        country.code.also { code ->
            mapSvg.getElementById(code)?.apply {
                val position = cases.size.takeIf { it > 0 }?.let { index.toDouble() / it } ?: 0.0
                val color = ColorRange.whiteToRed.colorAt(position)
                setAttribute("style", "fill: ${color.toPlatformColor()}")
            } ?: console.log("Didn't find $code")
        }
    }
}


private val mapSvg: Document
    get() = mapObject.getSVGDocument() ?: error("map object has no svg document")
private val mapObject: HTMLObjectElement
    get() = (document.querySelector(".map") as? HTMLObjectElement)
        ?: error("map object tag not found")