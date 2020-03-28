package org.virustrend.android

import android.graphics.Color
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.virustrend.R
import org.virustrend.android.utils.toColorBetween
import org.virustrend.android.utils.whenSvgElementReady
import org.virustrend.client.VirusTrendClient
import org.virustrend.country
import java.text.NumberFormat

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val client = VirusTrendClient()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            withContext(IO) {
                val totals = client.total()
                val countryCases = totals.countryCases
                withContext(Main) {
                    totals.cases.let { (confirmed, deaths, recovered, active) ->
                        confirmedView.setText(NumberFormat.getNumberInstance().format(confirmed))
                        deathsView.setText(deaths.toString())
                        recoveredView.setText(recovered.toString())
                        activeView.setText(active.toString())
                    }
                }
                Sharp.loadResource(resources, R.raw.world)
                    .whenSvgElementReady { id, paint ->
                        paint?.color = countryCases
                            .sortedBy { it.cases.confirmed }
                            .indexOfFirst { it.country?.code == id }
                            .takeIf { it > 0 }
                            ?.let { it / countryCases.size.toFloat() }
                            ?.asRedGradient() ?: Color.WHITE
                    }.into(mapView)
            }
        }
    }

}

@ColorInt
private fun Float.asRedGradient(): Int = toColorBetween(Color.WHITE, Color.RED)


