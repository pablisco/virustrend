import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.LinearDimension.Companion.fillAvailable
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

    override fun RBuilder.render() {
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
                backgroundColor = Color.aquamarine
            }
        }
        styledFooter {
            css {
                flex(flexGrow = 0.0, flexBasis = FlexBasis.fill)
                backgroundColor = Color.blueViolet
                minHeight = 60.px
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

}

private val appTag: Element =
    document.getElementsByTagName("app").asList().apply {
        check(isNotEmpty()) { "Could not find <app> tag in DOM." }
        check(size == 1) { "More than one <app> tag found in DOM." }
    }.first()

private fun applyGlobalStyles() {
    StyledComponents.injectGlobal {
        tag("app") {
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.spaceBetween
            alignItems = Align.stretch
            alignContent = Align.stretch
            height = fillAvailable
        }
        body {
            margin(0.px)
            padding(0.px)
        }
    }
}

private fun CSSBuilder.tag(tag: String, block: RuleSet): Rule =
    TagSelector(tag)(block)