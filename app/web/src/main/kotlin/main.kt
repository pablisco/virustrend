import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.virustrend.domain.AppState
import org.virustrend.domain.StateMachine
import org.virustrend.web.App
import org.w3c.dom.Element
import org.w3c.dom.asList
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

@FlowPreview
@ExperimentalCoroutinesApi
fun main() {
    window.onload = {
        render(appTag) {
            child(App::class) {
                attrs {
                    stateMachine = StateMachine()
                    virusTrendState = AppState()
                }
            }
        }
    }
}

private val appTag: Element =
    document.getElementsByTagName("app").asList().apply {
        check(isNotEmpty()) { "Could not find <app> tag in DOM." }
        check(size == 1) { "More than one <app> tag found in DOM." }
    }.first()
