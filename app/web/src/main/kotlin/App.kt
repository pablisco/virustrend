import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onLoadFunction
import org.virustrend.country
import org.virustrend.domain.SelectableCountry
import org.virustrend.domain.VirusTrendEvent
import org.virustrend.domain.VirusTrendState
import org.virustrend.domain.VirusTrendStateMachine
import org.w3c.dom.Element
import org.w3c.dom.asList
import react.*
import react.dom.option
import react.dom.render
import react.dom.select
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

private fun RBuilder.countrySelect(country: List<SelectableCountry>) =
    select {
        country.forEach {
            option {
                when (it) {
                    is SelectableCountry.None -> {
                        +"All Countries"
                        attrs["value"] = "ALL"
                    }
                    is SelectableCountry.Some -> {
                        +it.country.countryName
                        attrs["value"] = it.country.code
                    }
                }
            }
        }
    }

@FlowPreview
@ExperimentalCoroutinesApi
class App(props: AppProperties) : RComponent<AppProperties, AppState>(props) {

//    private fun List<CountryCases>.renderStyles() {
//        StyledComponents.injectGlobal {
//            svg {
//                mapNotNull { it.country?.code }.forEach {
//                    child("#$it") {
//                        color = Color.green
//                    }
//                }
//            }
//        }
//    }

    override fun RBuilder.render() {
//        StyledComponents.injectGlobal {
//            body {
//
//            }
//            state.virusTrendState.content?.casesByCountry
//                ?.mapNotNull { it.country?.code }
//                ?.forEach {
//                    rule("#$it") {
//
//                        color = Color.green
//                    }
//                }
//        }
//        state.virusTrendState.content?.casesByCountry?.renderStyles()
        styledHeader {
            css {
                flex(flexGrow = 0.0, flexBasis = FlexBasis.fill)
                minHeight = 60.px
            }
            state.virusTrendState.content?.countries?.let { countries ->
                countrySelect(countries)
            }
        }
        styledDiv {
            css {
                flex(flexGrow = 1.0, flexBasis = FlexBasis.fill)
                display = Display.flex
                backgroundColor = Color.aquamarine
                alignItems = Align.center
            }
            styledObject_ {
                attrs {
                    data = "world.svg"
                    type = "image/svg+xml"
                    width = "100%"
                    height = "100%"
                    onLoadFunction = {
                        state.virusTrendState.content?.casesByCountry?.forEach {
                            it.country?.code?.also { code ->
                                console.log(code)
//                                document.getElementById(code)?.
                            }
                        }
                    }
                }

                css {
//                    flex(flexGrow = 1.0, flexBasis = FlexBasis.fill)
//                    width = LinearDimension("100%")
//                    objectFit = ObjectFit.fill
                }
            }
        }
        styledFooter {
            css {
                flex(flexGrow = 0.0, flexBasis = FlexBasis.fill)
                backgroundColor = Color.blueViolet
                minHeight = 60.px

                display = Display.flex
                justifyContent = JustifyContent.spaceEvenly
                flexDirection = FlexDirection.column
                media("only screen and (min-width: 600px)") {
                    flexDirection = FlexDirection.row
                }
            }
            state.virusTrendState.content?.total
                ?.cases?.also { (confirmed, deaths, recovered, active) ->
                    infoBox("Confirmed", "$confirmed")
                    infoBox("Deaths", "$deaths")
                    infoBox("Recovered", "$recovered")
                    infoBox("Active", "$active")
                }
        }
    }

    private fun RBuilder.infoBox(
        hint: String,
        message: String
    ) = styledDiv {

        css {
            flexGrow = 1.0
        }
        +message
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
        body {
            margin(0.px)
            padding(0.px)
        }
    }
}

private fun tag(tag: String): TagSelector = TagSelector(tag)

private val app: TagSelector get() = tag("app")
private val path: TagSelector get() = tag("path")