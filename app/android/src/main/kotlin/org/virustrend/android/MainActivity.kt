package org.virustrend.android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.pixplicity.sharp.Sharp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.virustrend.CountryCases
import org.virustrend.R
import org.virustrend.android.utils.toColorBetween
import org.virustrend.android.utils.whenSvgElementReady
import org.virustrend.country
import org.virustrend.domain.Content
import org.virustrend.domain.VirusTrendEvent.Start
import org.virustrend.domain.VirusTrendState
import org.virustrend.domain.VirusTrendState.Initial
import org.virustrend.domain.VirusTrendStateMachine
import java.text.NumberFormat

@FlowPreview
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(viewModel) {
            start()
            stateMachine.states
                .onEach { state -> state.render() }
                .launchIn(lifecycleScope)
        }
    }

    private suspend fun VirusTrendState.render() = withContext(Main) {
        Log.d("VirusTrendState", this@render::class.simpleName!!)
        when (this@render) {
            is Initial -> Unit
            is VirusTrendState.Loading -> {
                loading.visibility = View.VISIBLE
                idle.visibility = View.GONE
            }
            is VirusTrendState.Idle -> {
                loading.visibility = View.GONE
                idle.visibility = View.VISIBLE
            }
            is VirusTrendState.Failed -> {
                loading.visibility = View.GONE
                idle.visibility = View.VISIBLE
                // TODO show SnackBar
            }
        }
        content?.render()
        content?.casesByCountry?.render()
    }

    private suspend fun List<CountryCases>.render() = withContext(Main) {
        Sharp.loadResource(resources, R.raw.world)
            .whenSvgElementReady { id, paint ->
                paint?.color = sortedBy { it.cases.confirmed }
                    .indexOfFirst { it.country?.code == id }
                    .takeIf { it > 0 }
                    ?.let { it / size.toFloat() }
                    ?.asRedGradient() ?: Color.WHITE
            }.into(mapView)
    }

    private suspend fun Content.render() = withContext(Main) {
        total.cases.let { (confirmed, deaths, recovered, active) ->
            confirmedView.setText(confirmed.formatted())
            deathsView.setText(deaths.formatted())
            recoveredView.setText(recovered.formatted())
            activeView.setText(active.formatted())
        }
    }

}

private fun Int.formatted(): String = NumberFormat.getNumberInstance().format(this)

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel(
    val stateMachine: VirusTrendStateMachine = VirusTrendStateMachine()
) : ViewModel() {

    fun start() {
        viewModelScope.launch {
            stateMachine.notify(Start)
        }
    }

}

@ColorInt
private fun Float.asRedGradient(): Int = toColorBetween(Color.WHITE, Color.RED)


