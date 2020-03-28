package org.virustrend.android

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.virustrend.R
import org.virustrend.android.utils.onSvgElement
import org.virustrend.client.VirusTrendClient
import org.virustrend.country

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
                    output.text = "${totals.cases}"
                }
                val svg = Sharp.loadResource(resources, R.raw.world)
                svg.onSvgElement { id, paint ->
                    val p = countryCases
                        .sortedBy { it.cases.confirmed }
                        .indexOfFirst { it.country?.code == id }
                        .takeIf { it > 0 }
                        ?.let { it / countryCases.size.toFloat() }
                    paint?.color = p?.asGradientRed() ?: Color.WHITE
                }
                svg.into(mapView)
            }
        }
    }

}

private val argbEvaluator = ArgbEvaluator()

@SuppressLint("NewApi")
private val midRed: Int = Color.valueOf(1f, .5f, 0.5f).toArgb()

//@SuppressLint("NewApi")
@ColorInt
private fun Float.asGradientRed(): Int {
    return argbEvaluator.evaluate(this, Color.WHITE, Color.RED) as Int
}


