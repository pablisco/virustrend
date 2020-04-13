import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.mCircularProgress
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onLoadFunction
import org.virustrend.CountryCases
import org.virustrend.color.ColorRange
import org.virustrend.color.colorAt
import org.virustrend.color.toPlatformColor
import org.virustrend.country
import org.virustrend.domain.VirusTrendEvent
import org.virustrend.domain.VirusTrendState
import org.virustrend.domain.VirusTrendStateMachine
import org.virustrend.web.toLocaleString
import org.virustrend.web.visibleIf
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLObjectElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import react.*
import react.dom.render
import styled.*
import kotlin.browser.document

@FlowPreview
@ExperimentalCoroutinesApi
fun startApp() {
    render(appTag) {
        child(App::class) {
            attrs {
                stateMachine = VirusTrendStateMachine()
                virusTrendState = VirusTrendState.Loading()
            }
        }
    }
}

interface AppState : RState {
    var virusTrendState: VirusTrendState
}

@FlowPreview
@ExperimentalCoroutinesApi
interface AppProperties : RProps {
    var virusTrendState: VirusTrendState
    var stateMachine: VirusTrendStateMachine
}

@FlowPreview
@ExperimentalCoroutinesApi
class App(props: AppProperties) : RComponent<AppProperties, AppState>(props) {

    override fun RBuilder.render() {
        mCssBaseline()
        mThemeProvider(
            theme = createMuiTheme().apply {
                palette.primary.main = "#fff"
            }
        ) {
            if (!state.hasContent) {
                mCircularProgress {
                    css {
                        position = Position.absolute
                        left = 0.px
                        top = 0.px
                        right = 0.px
                        bottom = 0.px
                        margin = "auto"
                    }
                }
            } else {
                styledDiv {
                    css {
//                        visibleIf { state.hasContent }
                        flex(flexGrow = 1.0, flexBasis = FlexBasis.fill)
                        display = Display.flex
                        backgroundColor = Color.aquamarine
                        alignItems = Align.center
                    }
                }
                mapGraph(cases = state.virusTrendState.content?.casesByCountry ?: emptyList())
                styledFooter {
                    css {
//                        visibleIf { state.hasContent }
                        flex(flexGrow = 0.0, flexBasis = FlexBasis.fill)
                        margin(8.px)
                        display = Display.flex
                        justifyContent = JustifyContent.spaceEvenly
                        flexDirection = FlexDirection.column
                        media("only screen and (min-width: 600px)") {
                            flexDirection = FlexDirection.row
                        }
                    }
                    state.virusTrendState.content?.total
                        ?.cases?.also { (confirmed, deaths, recovered, active) ->
                            counterBox("Confirmed", confirmed)
                            counterBox("Deaths", deaths)
                            counterBox("Recovered", recovered)
                            counterBox("Active", active)
                        }
                }
            }
        }
    }

    override fun AppState.init(props: AppProperties) {
        applyGlobalStyles()
        virusTrendState = props.virusTrendState
        GlobalScope.launch {
            props.stateMachine.notify(VirusTrendEvent.Start)
        }
        GlobalScope.launch {
            props.stateMachine.states.collect {
                setState { virusTrendState = it }
            }
        }
    }


    private fun RBuilder.mapGraph(cases: List<CountryCases>) = styledObject_ {
        css {
            visibleIf { state.hasContent }
        }
        attrs {
            classes += "map"
            data = "world.svg"
            type = "image/svg+xml"
            width = "100%"
            height = "100%"
            onLoadFunction = renderMapGraph(cases)
        }
    }

}

private fun RBuilder.counterBox(name: String, count: Int) = styledDiv {
    css {
        flexGrow = 1.0
        margin(8.px)
    }
    mCard {
        css {
            padding(16.px)
        }
        styledDiv {
            css {
                fontSize = 16.px
                textAlign = TextAlign.center
            }
            +name
        }
        styledDiv {
            css {
                fontSize = 32.px
                textAlign = TextAlign.center
            }
            +"${count.toLocaleString()}"
        }
    }
}

private fun renderMapGraph(cases: List<CountryCases>): (Event) -> Unit = {
    cases.sortedBy { it.cases.confirmed }.forEachIndexed { index, case ->
        case.country?.code?.also { code ->
            mapSvg.getElementById(code)?.apply {
                val position = cases.size.takeIf { it > 0 }
                    ?.let { index.toDouble() / it } ?: 0.0
                val color = ColorRange.whiteToRed.colorAt(position)

                setAttribute("style", "fill: ${color.toPlatformColor()}")
            } ?: console.log("Didn't find $code")
        }
    }
}

private val appTag: Element =
    document.getElementsByTagName("app").asList().apply {
        check(isNotEmpty()) { "Could not find <app> tag in DOM." }
        check(size == 1) { "More than one <app> tag found in DOM." }
    }.first()

private fun applyGlobalStyles() {
    StyledComponents.injectGlobal {
        app {
            display = Display.flex
            position = Position.relative
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.spaceBetween
            alignItems = Align.stretch
            alignContent = Align.stretch
            height = 100.vh
            width = 100.vw
        }
    }
}

private fun tag(tag: String): TagSelector = TagSelector(tag)

private val app: TagSelector get() = tag("app")
private val mapSvg: Document
    get() = mapObject.getSVGDocument() ?: error("map object has no svg document")
private val mapObject: HTMLObjectElement
    get() = (document.querySelector(".map") as? HTMLObjectElement)
        ?: error("map object tag not found")

private val AppState.hasContent: Boolean get() = virusTrendState.content != null
