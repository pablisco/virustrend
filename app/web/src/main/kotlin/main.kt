import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.virustrend.client.VirusTrendClient
import org.virustrend.country
import kotlin.browser.document

fun main() {
    GlobalScope.launch {
        val countries = VirusTrendClient().total().countryCases.map { it.country }
        document.write(countries.joinToString("\n"))
    }
}