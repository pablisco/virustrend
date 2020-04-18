package org.virustrend.web

import com.ccfraser.muirwik.components.mCircularProgress
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mLinearProgress
import com.ccfraser.muirwik.components.mThemeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.css.*
import org.virustrend.domain.*
import org.virustrend.web.screens.renderWorldMap
import org.virustrend.web.style.appTheme
import org.virustrend.web.views.toolbar
import react.*
import styled.StyledBuilder
import styled.StyledComponents
import styled.css
import styled.injectGlobal

interface RAppState : RState {
    var appState: AppState
}

@FlowPreview
@ExperimentalCoroutinesApi
interface AppProperties : RProps {
    var virusTrendState: AppState
    var stateMachine: StateMachine
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
        mThemeProvider(theme = appTheme) {
            mCssBaseline()
            renderApp(state.appState, props)
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

@FlowPreview
@ExperimentalCoroutinesApi
private fun RBuilder.renderApp(state: AppState, props: AppProperties) {
    val changeCountryEvents = toolbar(state)
    GlobalScope.launch {
        changeCountryEvents
            .onEach { event -> props.stateMachine.notify(event) }
            .collect()
    }
    when (val screen = state.screen) {
        is Async.Loading.Fresh -> mCircularProgress { centerOnScreen() }
        is Async.Loading.WithData -> mLinearProgress()
        else -> screen.maybeData {
            when (this) {
                is Screen.WorldMap -> renderWorldMap(this)
            }
        }
    }
}

private fun applyGlobalStyles() {
    StyledComponents.injectGlobal {
        app {
            display = Display.flex
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
