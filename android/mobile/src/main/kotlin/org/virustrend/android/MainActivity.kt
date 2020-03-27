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
import org.virustrend.android.utils.onSvgElementDrawn
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
                val maxConfirmed = countryCases.map { it.cases.confirmed }.max() ?: 0
                val svg = Sharp.loadResource(resources, R.raw.world)
                svg.onSvgElementDrawn { id, paint ->
                    countryCases.find { it.country?.code == id }?.also {
                        val p = it.cases.confirmed.toFloat() / maxConfirmed.toFloat()
                        paint?.color = p.asGradientRed()
                    }
                }
                svg.into(mapView)
            }
        }
    }

}

private val argbEvaluator = ArgbEvaluator()

@SuppressLint("NewApi")
@ColorInt
private fun Float.asGradientRed(): Int {
    val midRed: Int = Color.valueOf(1f, .5f, 0.5f).toArgb()
    return argbEvaluator.evaluate(this, midRed, Color.RED) as Int
}


