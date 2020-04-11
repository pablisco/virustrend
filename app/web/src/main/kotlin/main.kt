import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.browser.window

@FlowPreview
@ExperimentalCoroutinesApi
fun main() {
    window.onload = { startApp() }
}
