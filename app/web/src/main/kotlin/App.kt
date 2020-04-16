import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.card.mCard
import com.ccfraser.muirwik.components.form.mFormControl
import com.ccfraser.muirwik.components.menu.mMenuItem
import com.ccfraser.muirwik.components.styles.createMuiTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.classes
import kotlinx.html.js.onLoadFunction
import org.virustrend.Country
import org.virustrend.color.ColorRange
import org.virustrend.color.colorAt
import org.virustrend.color.toPlatformColor
import org.virustrend.domain.*
import org.virustrend.domain.AppEvent.ChangeCountry
import org.virustrend.domain.CountrySelection.All
import org.virustrend.domain.CountrySelection.Target
import org.virustrend.domain.Screen
import org.virustrend.web.toLocaleString
import org.w3c.dom.*
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
                stateMachine = StateMachine()
                virusTrendState = AppState(screen = Async.Loading.Fresh)
            }
        }
    }
}

interface RAppState : RState {
    var appState: AppState
}

@FlowPreview
@ExperimentalCoroutinesApi
interface AppProperties : RProps {
    var virusTrendState: AppState
    var stateMachine: StateMachine
}

@ExperimentalCoroutinesApi
private fun RBuilder.toolbar(state: AppState): Flow<AppEvent> = callbackFlow {
    mToolbar {
        css {
            backgroundColor = Color.darkGray
        }
        state.countries.maybeData?.also { countries ->
            mFormControl {
                mSelect(
                    value = state.countrySelection.code,
                    onChange = { event, _ ->
                        val code = (event.target as? HTMLInputElement)?.value
                        val selectedCountry = countries.firstOrNull { it.code == code } ?: All
                        offer(ChangeCountry(selectedCountry))
                    }
                ) {
                    countries.forEach { mMenuItem(value = it.code) { +it.label } }
                }
            }
        }
    }
}

private val appTheme = createMuiTheme().apply {
    palette.primary.main = "#fff"
}

private fun StyledBuilder<*>.centerOnScreen() {
    css {
        position = Position.absolute
        left = 0.px
        top = 0.px
        right = 0.px
        bottom = 0.px
        margin = "auto"
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
class App(props: AppProperties) : RComponent<AppProperties, RAppState>(props) {

    override fun RBuilder.render() {
        mCssBaseline()
        mThemeProvider(theme = appTheme) {
            toolbar(state.appState)
            when (val screen = state.appState.screen) {
                is Async.Loading.Fresh -> mCircularProgress { centerOnScreen() }
                is Async.Loading.WithData -> mLinearProgress()
                else -> screen.maybeData?.let { render(it) }
            }
        }
    }

    override fun RAppState.init(props: AppProperties) {
        applyGlobalStyles()
        appState = props.virusTrendState
        GlobalScope.launch {
            props.stateMachine.notify(AppEvent.StartMapScreen)
        }
        GlobalScope.launch {
            props.stateMachine.states.collect {
                setState { appState = it }
            }
        }
    }

}

private fun RBuilder.render(screen: Screen) = when (screen) {
    is Screen.WorldMap -> render(screen)
}

private fun RBuilder.render(screen: Screen.WorldMap) = with(screen) {
    mapGraph(cases = countryToMetric)
    styledFooter {
        css {
            flex(flexGrow = 0.0)
            margin(8.px)
            display = Display.flex
            justifyContent = JustifyContent.spaceEvenly
            flexDirection = FlexDirection.column
            media("only screen and (min-width: 600px)") {
                flexDirection = FlexDirection.row
            }
        }
        val (confirmed, deaths, recovered, active) = cases
        counterBox("Confirmed", confirmed)
        counterBox("Deaths", deaths)
        counterBox("Recovered", recovered)
        counterBox("Active", active)
    }
}

private val CountrySelection.label: String
    get() = if (this is Target) country.name else "All Countries"

private val CountrySelection.code: String
    get() = if (this is Target) country.code else "ALL"

private fun RBuilder.mapGraph(cases: List<Pair<Country, Int>>) = styledObject_ {
    css {
        flex(flexGrow = 1.0)
    }
    attrs {
        classes += "map"
        data = "world.svg"
        type = "image/svg+xml"
//        width = "100%"
//        height = 100.pct.toString()
        onLoadFunction = renderMapGraph(cases)
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
